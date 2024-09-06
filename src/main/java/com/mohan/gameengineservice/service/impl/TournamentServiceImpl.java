package com.mohan.gameengineservice.service.impl;

import com.mohan.gameengineservice.dto.*;
import com.mohan.gameengineservice.entity.*;
import com.mohan.gameengineservice.entity.constants.MatchStage;
import com.mohan.gameengineservice.entity.constants.TournamentStatus;
import com.mohan.gameengineservice.repository.*;
import com.mohan.gameengineservice.service.TournamentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TournamentServiceImpl implements TournamentService {
    public TournamentServiceImpl(TournamentRepository tournamentRepository, TeamRegistrationRepository teamRegistrationRepository, TeamRepository teamRepository, CoachRepository coachRepository, LocationRepository locationRepository, CricketMatchRepository cricketMatchRepository) {
        this.tournamentRepository = tournamentRepository;
        this.teamRegistrationRepository = teamRegistrationRepository;
        this.teamRepository = teamRepository;
        this.coachRepository = coachRepository;
        this.locationRepository = locationRepository;
        this.cricketMatchRepository = cricketMatchRepository;
    }

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRegistrationRepository teamRegistrationRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private  CricketMatchRepository cricketMatchRepository;



//    public void registerTeam(Long  tournamentId, TeamRegistrationDTO registrationDTO) throws Exception {
////        Long tournamentId = registrationDTO.getTournamentId();
//        Tournament tournament = tournamentRepository.findById(tournamentId)
//                .orElseThrow(() -> new Exception("Tournament not found"));
//
//        // Assuming you have methods to find teams and coaches by details
//        Team team = teamRepository.findById(registrationDTO.getId())
//                .orElseThrow(() -> new Exception("Team not found"));
//
////        Coach coach = coachRepository.findByName(registrationDTO.getCoach())
////                .orElseThrow(() -> new Exception("Coach not found"));
//
//        TeamRegistration registration = new TeamRegistration();
//        registration.setTournament(tournament);
//        registration.setTeam(team);
//        registration.setCoachName(registrationDTO.getCoach());
//        registration.setRegistrationDate(LocalDateTime.now());
//
//        teamRegistrationRepository.save(registration);
//
//    }


    // this is for the registered teams for the tournament
    public List<TeamSummaryDTO> getRegisteredTeams(Long tournamentId) {
        List<TeamRegistration> registrations = teamRegistrationRepository.findTeamRegistrationByTournamentId(tournamentId);

        // Map the registrations to TeamSummary
                    return registrations.stream()
                            .map(registration -> {
                                Team team = registration.getTeam();
                                return new TeamSummaryDTO(
                                        Math.toIntExact(team.getTeamId()),  // Convert Long to Integer
                                        team.getName(),
                                        team.getCountry(),
                                        team.getTeamCaptain(),
                                        team.getCoach(), // Assuming Coach's name or String representation
                                        team.getOwner()
                                );
                            })
                            .collect(Collectors.toList());
    }

//    public List<TeamSummary> getRegisteredTeams(Long tournamentId) {
//        // Retrieve team summaries using the projection interface
//        return teamRegistrationRepository.findTeamSummariesByTournamentId(tournamentId);
//    }




    @Transactional
    public List<MatchDetailsDTO> scheduleRoundRobinMatches(Long tournamentId) {
        // Fetch the tournament
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Fetch team registrations and map to teams
        List<TeamRegistration> teamRegistrations = teamRegistrationRepository.findTeamRegistrationByTournamentId(tournamentId);
        List<Team> teams = teamRegistrations.stream().map(TeamRegistration::getTeam).collect(Collectors.toList());

        // Fetch all locations
        List<Location> allLocations = locationRepository.findAll();

        if (allLocations.isEmpty()) {
            throw new RuntimeException("No locations available for scheduling matches.");
        }

        // Initialize a list to keep track of used location IDs
        Set<Long> usedLocationIds = new HashSet<>();

        int numberOfTeams = teams.size();
        LocalDateTime matchDateTime = tournament.getStartDate();
        List<MatchDetailsDTO> matchDetails = new ArrayList<>();

        for (int i = 0; i < numberOfTeams; i++) {
            for (int j = i + 1; j < numberOfTeams; j++) {
                Team teamA = teams.get(i);
                Team teamB = teams.get(j);

                // Avoid scheduling matches during night time
                while (matchDateTime.getHour() < 6 || matchDateTime.getHour() > 20) {
                    matchDateTime = matchDateTime.plusDays(1).withHour(10);
                }

                // Check if match already exists
                boolean matchExists = cricketMatchRepository.existsByTeamAAndTeamBAndMatchDateTime(
                        teamA, teamB, matchDateTime);

                if (matchExists) {
                    continue; // Skip creating this match if it already exists
                }

                // Find an unused location
                Location location = findUnusedLocation(allLocations, usedLocationIds);

                if (location == null) {
                    throw new RuntimeException("Not enough unique locations available for all matches.");
                }

                // Mark location as used
                usedLocationIds.add(location.getId());

                // Create and save the match
                CricketMatch match = new CricketMatch();
                match.setTeamA(teamA);
                match.setTeamB(teamB);
                match.setMatchType(tournament.getTournamentName());
                match.setMatchDateTime(matchDateTime);
                match.setTournament(tournament);
                match.setLocation(location);

                cricketMatchRepository.save(match);

                // Create MatchDetailsDTO
                MatchDetailsDTO matchDetail = new MatchDetailsDTO();
                matchDetail.setMatchId(match.getId());
                matchDetail.setTeamA(teamA.getName());
                matchDetail.setTeamB(teamB.getName());
                matchDetail.setMatchDateTime(matchDateTime);
                matchDetail.setLocation(location.getCountry() + " - " + location.getGround());

                matchDetails.add(matchDetail);

                // Move to the next match date
                matchDateTime = matchDateTime.plus(tournament.getMatchInterval());
            }
        }

        // Update tournament status
        tournament.setStatus(TournamentStatus.ONGOING);
        tournamentRepository.save(tournament);

        return matchDetails;
    }

    /**
     * Find an unused location that has not been assigned to any previous match.
     */
    private Location findUnusedLocation(List<Location> allLocations, Set<Long> usedLocationIds) {
        for (Location location : allLocations) {
            if (!usedLocationIds.contains(location.getId())) {
                return location;
            }
        }
        return null; // No unused locations found
    }


    @Override
    public Tournament createTournament(TournamentDTO tournamentDTO) {
        if (tournamentDTO.getNumberOfTeams() < 6) {
            throw new IllegalArgumentException("At least 6 teams are required for the tournament");
        }

        Tournament tournament = Tournament.builder()
                .tournamentName(tournamentDTO.getName())
                .location(tournamentDTO.getLocation())
                .startDate(tournamentDTO.getStartDate())
                .matchInterval(tournamentDTO.getMatchInterval())
                .numberOfTeams(tournamentDTO.getNumberOfTeams())
                .status(TournamentStatus.PLANNED)
                .build();

        return tournamentRepository.save(tournament);
    }


    // this for the easy conversion  to entity
    public Tournament convertToEntity(TournamentDTO dto) {
        Tournament tournament = new Tournament();
        tournament.setTournamentName(dto.getName());
        tournament.setLocation(dto.getLocation());
        tournament.setStartDate(dto.getStartDate());
        tournament.setMatchInterval(dto.getMatchInterval());
        tournament.setNumberOfTeams(dto.getNumberOfTeams());
        // Set default status if needed
        tournament.setStatus(TournamentStatus.PLANNED);
        return tournament;
    }

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
                tournament.getLocation(),
                tournament.getStartDate(),
                tournament.getMatchInterval(),
                tournament.getNumberOfTeams(),
                registeredTeamsCount, // Pass the count of registered teams
                tournament.getStatus() // Pass the status of the tournament
        );
    }


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
        registration.setTeam(team); // Assuming a constructor for simplicity
        registration.setCoachName(teamRegistrationDTO.getCoach());
        registration.setRegistrationDate(LocalDate.now().atStartOfDay()); // Example of setting registration date

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



}
