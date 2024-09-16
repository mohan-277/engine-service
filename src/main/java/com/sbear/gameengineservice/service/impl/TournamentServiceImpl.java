package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.*;
import com.sbear.gameengineservice.entity.constants.TournamentStatus;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.entity.stats.TeamStats;
import com.sbear.gameengineservice.repository.*;
import com.sbear.gameengineservice.repository.stats.PlayerStatsRepository;
import com.sbear.gameengineservice.repository.stats.TeamStatsRepository;
import com.sbear.gameengineservice.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TournamentServiceImpl implements TournamentService {

    private final TeamStatsRepository teamStatsRepository;

    private final BallRepository ballRepository;

    private final  InningsRepository inningsRepository;

    private final TournamentRepository tournamentRepository;

    private final TeamRegistrationRepository teamRegistrationRepository;

    private final TeamRepository teamRepository;

    private  final CricketMatchRepository cricketMatchRepository;

    private final PlayerStatsRepository playerStatsRepository;

    public TournamentServiceImpl(TeamStatsRepository teamStatsRepository, BallRepository ballRepository, InningsRepository inningsRepository, TournamentRepository tournamentRepository, TeamRegistrationRepository teamRegistrationRepository,
                                 TeamRepository teamRepository,CricketMatchRepository cricketMatchRepository, PlayerStatsRepository playerStatsRepository) {
        this.teamStatsRepository = teamStatsRepository;
        this.ballRepository = ballRepository;
        this.inningsRepository = inningsRepository;

        this.tournamentRepository = tournamentRepository;
        this.teamRegistrationRepository = teamRegistrationRepository;
        this.teamRepository = teamRepository;
        this.cricketMatchRepository = cricketMatchRepository;
        this.playerStatsRepository = playerStatsRepository;
    }


    /**
     * Retrieves a list of team summaries for a specific tournament.
     * Converts the list of team registrations into a list of TeamSummaryDTO objects.
     *
     * @param tournamentId the ID of the tournament for which teams are being retrieved
     * @return a list of TeamSummaryDTO objects containing details of registered teams
     */

    public List<TeamSummaryDTO> getRegisteredTeams(Long tournamentId) {
        List<TeamRegistration> registrations = teamRegistrationRepository.findTeamRegistrationByTournamentId(tournamentId);

                    return registrations.stream()
                            .map(registration -> {
                                Team team = registration.getTeam();
                                return new TeamSummaryDTO(
                                        Math.toIntExact(team.getTeamId()),
                                        team.getName(),
                                        team.getCountry(),
                                        team.getTeamCaptain(),
                                        team.getCoachName(),
                                        team.getOwner()
                                );
                            })
                            .collect(Collectors.toList());
    }

    /**
     * Admin Creates and saves a new tournament if at least six teams are specified.
     */

    @Override
    public Tournament createTournament(TournamentDTO tournamentDTO) {
        if (tournamentDTO.getNumberOfTeams() < 6) {
            throw new IllegalArgumentException("At least 6 teams are required for the tournament");
        }

        Tournament tournament = Tournament.builder()
                .tournamentName(tournamentDTO.getTournamentName())
                .tournamentType(tournamentDTO.getTournamentType())
                .location(tournamentDTO.getLocation())
                .startDate(LocalDateTime.parse(tournamentDTO.getStartDate()))
                .matchInterval(tournamentDTO.getMatchInterval())
                .numberOfTeams(tournamentDTO.getNumberOfTeams())
                .status(TournamentStatus.PLANNED)
                .build();

        return tournamentRepository.save(tournament);
    }

    /**
     * Retrieves and converts all tournaments from the repository into a list of TournamentDTOs.
     */

    public List<TournamentDTO> getAllTournaments(){
        List<Tournament> tournaments = tournamentRepository.findAll();
        return tournaments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TournamentDTO convertToDto(Tournament tournament) {
        int registeredTeamsCount = tournament.getTeamRegistrations().size();
        return new TournamentDTO(
                tournament.getId(),
                tournament.getTournamentName(),
                tournament.getTournamentType(),
                tournament.getLocation(),
                tournament.getStartDate() == null ? null : tournament.getStartDate().toString(),
                tournament.getMatchInterval(),
                tournament.getNumberOfTeams(),
                registeredTeamsCount,
                tournament.getStatus()
        );
    }


    /**
     * Registers a team for a tournament using the provided tournament ID and team registration details.
     * Throws an exception if the tournament is not found or is invalid for registration.
     */

    public String registerTeamByTournamentID(Long tournamentId, TeamRegistrationDTO teamRegistrationDTO) throws Exception {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (tournament.getNumberOfTeams() < 6) {
            throw new IllegalArgumentException("Tournament not valid for registration");
        }

        // Check if the tournament has reached its team limit
        if (tournament.getNumberOfTeams() <= tournament.getTeamRegistrations().size()) {
            throw new IllegalArgumentException("Tournament is full. Registration not allowed.");
        }
        Team team = teamRepository.findById(teamRegistrationDTO.getTeamID())
                .orElseThrow(() -> new Exception("Team not found"));


        // Check if the team is already registered for the tournament
        boolean isAlreadyRegistered = teamRegistrationRepository.existsByTournamentIdAndTeamTeamId(tournamentId, team.getTeamId());
        if (isAlreadyRegistered) {
            throw new IllegalArgumentException("The team is already registered for this tournament");
        }


        TeamRegistration registration = new TeamRegistration();
        registration.setTournament(tournament);
        registration.setTeam(team);
        registration.setCoachName(teamRegistrationDTO.getCoachName());
        registration.setRegistrationDate(LocalDate.now().atStartOfDay());

        // Validate group assignment or assign default
        String groupType = teamRegistrationDTO.getGroupType();
        if (groupType == null || groupType.isEmpty()) {
            groupType = determineDefaultGroup(tournamentId);
        }

        long groupACount = teamRegistrationRepository.countByTournamentIdAndGroupType(tournamentId, "Group A");
        long groupBCount = teamRegistrationRepository.countByTournamentIdAndGroupType(tournamentId, "Group B");

        if ("Group A".equals(groupType) && groupACount >= tournament.getNumberOfTeams() / 2) {
            throw new IllegalArgumentException("Group A is full");
        }
        if ("Group B".equals(groupType) && groupBCount >= tournament.getNumberOfTeams() / 2) {
            throw new IllegalArgumentException("Group B is full");
        }

        registration.setGroupType(groupType);
        teamRegistrationRepository.save(registration);

        return "Team registered successfully in " + groupType;
    }

    private String determineDefaultGroup(Long tournamentId) {
        long groupACount = teamRegistrationRepository.countByTournamentIdAndGroupType(tournamentId, "Group A");
        long groupBCount = teamRegistrationRepository.countByTournamentIdAndGroupType(tournamentId, "Group B");

        return groupACount <= groupBCount ? "Group A" : "Group B";
    }

    /// get tournamentDTO by tournamentId

    public TournamentDTO getTournamentById(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).get();
        return convertToDto(tournament);
    }


    /// get MatchDetailsDTO by The matchID

    public MatchDetailsDTO getCricketMatchById(Long matchId) {
        CricketMatch cricketMatch = cricketMatchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));

        return matchToMatchDetailDTOConverter(cricketMatch);
    }

    private MatchDetailsDTO matchToMatchDetailDTOConverter(CricketMatch cricketMatch) {
        if (cricketMatch == null) {
            throw new IllegalArgumentException("CricketMatch cannot be null");
        }

        MatchDetailsDTO matchDetailsDTO = new MatchDetailsDTO();

        matchDetailsDTO.setMatchGroup(cricketMatch.getMatchGroup() != null ? cricketMatch.getMatchGroup() : "Unknown");
        matchDetailsDTO.setMatchType(cricketMatch.getMatchType() != null ? cricketMatch.getMatchType() : "Unknown");
        matchDetailsDTO.setMatchId(cricketMatch.getId());
        matchDetailsDTO.setMatchStage(cricketMatch.getMatchStage() != null ? cricketMatch.getMatchStage() : "Unknown");

        if (cricketMatch.getTeamA() != null && cricketMatch.getTeamA().getName() != null) {
            matchDetailsDTO.setTeamA(cricketMatch.getTeamA().getName());
        } else {
            matchDetailsDTO.setTeamA("Unknown");
        }

        if (cricketMatch.getTeamB() != null && cricketMatch.getTeamB().getName() != null) {
            matchDetailsDTO.setTeamB(cricketMatch.getTeamB().getName());
        } else {
            matchDetailsDTO.setTeamB("Unknown");
        }

        if (cricketMatch.getLocation() != null && cricketMatch.getLocation().getCountry() != null) {
            matchDetailsDTO.setLocation(cricketMatch.getLocation().getCountry());
        } else {
            matchDetailsDTO.setLocation("Unknown");
        }

        matchDetailsDTO.setMatchDateTime(cricketMatch.getMatchDateTime());
        matchDetailsDTO.setMatchStatus(cricketMatch.getMatchStatus() != null ? cricketMatch.getMatchStatus() : "Unknown");
        matchDetailsDTO.setLive(cricketMatch.isLive());

        return matchDetailsDTO;
    }

    /// Retrieves all player stats for the given match ID

   public List<PlayerStats> getALlPlayerStats(Long matchId){
        return playerStatsRepository.findByCurrentPlayingMatchId(matchId);
    }

    /// Retrieves all team stats for the given match ID
    public List<TeamStats> getAllTeamStats(Long matchId){
        return  teamStatsRepository.findTeamStatsByMatchId(matchId);
    }


    /// Retrieves all team stats for the specified match group type
    public List<TeamStats> getAllTeamStatsByMatchGroup(String matchGroupType){
        return teamStatsRepository.findTeamStatsByMatchGroup(matchGroupType);
    }


    /// Retrieves all matches available in the match repository
    public List<MatchDetailsDTO> getAllMatches() {
        List<CricketMatch> cricketMatch = cricketMatchRepository.findAll();
        return matchToMatchDetailDTOConverter(cricketMatch);

    }

    public List<MatchDetailsDTO> matchToMatchDetailDTOConverter(List<CricketMatch> cricketMatches) {
        return cricketMatches.stream()
                .map(match -> MatchDetailsDTO.builder()
                        .matchId(match.getId())
                        .matchType(match.getMatchType())
                        .matchGroup(match.getMatchGroup())
                        .location(match.getLocation() != null && match.getLocation().getCountry() != null
                                ? match.getLocation().getCountry()
                                : "Unknown")
                        .matchDateTime(match.getMatchDateTime())
                        .matchStage(match.getMatchStage())
                        .matchStatus(match.getMatchStatus())
                        .teamA(match.getTeamA() != null && match.getTeamA().getName() != null
                                ? match.getTeamA().getName()
                                : "Unknown")
                        .teamB(match.getTeamB() != null && match.getTeamB().getName() != null
                                ? match.getTeamB().getName()
                                : "Unknown")
                        .live(match.isLive())
                        .build())
                .collect(Collectors.toList());
    }


    /// Get match detail by the matchID

    public MatchResponseDTO getMatchDetails(Long matchId) {
        List<Innings> inningsList = inningsRepository.findByCricketMatchId(matchId);

        // Prepare the response DTO
        MatchResponseDTO response = new MatchResponseDTO();
        response.setMatchId(matchId);
        response.setInnings(new ArrayList<>());

        // For each inning, fetch the balls and populate the DTO
        for (Innings innings : inningsList) {
            InningsDTO inningsDTO = new InningsDTO();
            inningsDTO.setInningsId(innings.getId());
            inningsDTO.setInningsNumber(innings.getInningsNumber());
            inningsDTO.setBattingTeamName(innings.getBattingTeam() != null ? innings.getBattingTeam().getName() : null);
            inningsDTO.setBowlingTeamName(innings.getBowlingTeam() != null ? innings.getBowlingTeam().getName() : null);

            List<Ball> balls = ballRepository.findByInningsId(innings.getId());
            List<BallDTO> ballDTOs = balls.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            inningsDTO.setBalls(ballDTOs);
            response.getInnings().add(inningsDTO);
        }

        return response;
    }

    private BallDTO convertToDTO(Ball ball) {
        return BallDTO.builder()
                .ballId(ball.getId())
                .InningId(ball.getInnings().getId())
                .overNumber(ball.getOverNumber())
                .ballNumber(ball.getBallNumber())
                .striker(ball.getStrikerName())
                .nonStriker(ball.getNonStrikerName())
                .bowler(ball.getBowlerName())
                .ballType(ball.getBallType())
                .runsScored(ball.getRunsScored())
                .wicket(ball.getWicketType())
                .totalScore(ball.getTotalScore())
                .wicketNumber(ball.getWicketCount())
                .build();
    }






}
