package com.mohan.gameengineservice.service.impl;

import com.mohan.gameengineservice.dto.MatchDetailsDTO;
import com.mohan.gameengineservice.entity.*;
import com.mohan.gameengineservice.entity.constants.MatchStage;
import com.mohan.gameengineservice.entity.constants.TournamentStatus;
import com.mohan.gameengineservice.repository.CricketMatchRepository;
import com.mohan.gameengineservice.repository.LocationRepository;
import com.mohan.gameengineservice.repository.TeamRegistrationRepository;
import com.mohan.gameengineservice.repository.TournamentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchSchedulingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CricketMatchRepository cricketMatchRepository;

    @Autowired
    private TeamRegistrationRepository teamRegistrationRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private LocationRepository locationRepository;


//    // it will give the matches list which type of match it is so listing the matches  method as per required play-offs, semifinals, finals
//    public Map<String, Map<String, List<MatchDetailsDTO>>> getMatchesByTypeAndGroup(Long tournamentId) {
//        List<CricketMatch> allMatches = cricketMatchRepository.findCricketMatchesByTournamentId(tournamentId);
//
//        // Initialize result map
//        Map<String, Map<String, List<MatchDetailsDTO>>> result = new HashMap<>();
//        result.put("playoffs", new HashMap<>());
//        result.put("semifinals", new HashMap<>());
//        result.put("finals", new HashMap<>());
//
//        // Fetch team groups
//        Map<Long, String> teamGroupMap = teamRegistrationRepository.findTeamGroupMapByTournamentId(tournamentId);
//
//        // Separate matches by type and group
//        for (CricketMatch match : allMatches) {
//            String groupA = teamGroupMap.get(match.getTeamA().getTeamId());
//            String groupB = teamGroupMap.get(match.getTeamB().getTeamId());
//
//            MatchDetailsDTO matchDetail = convertToMatchDetailsDTO(match);
//
//            String groupKey = "Group A".equals(groupA) ? "Group A" : "Group B";
//            String matchType = match.getMatchType();
//
//            if (result.containsKey(matchType)) {
//                result.get(matchType).computeIfAbsent(groupKey, k -> new ArrayList<>()).add(matchDetail);
//            }
//        }
//
//        return result;
//    }

//    public Map<String, Map<String, List<MatchDetailsDTO>>> getMatchesByTypeAndGroup(Long tournamentId) {
//
//        List<CricketMatch> allMatches = cricketMatchRepository.findCricketMatchesByTournamentId(tournamentId);
//
//        // Initialize result map
//        Map<String, Map<String, List<MatchDetailsDTO>>> result = new HashMap<>();
//        result.put("playoffs", new HashMap<>());
//        result.put("semifinals", new HashMap<>());
//        result.put("finals", new HashMap<>());
//
//        // Fetch team groups
//        Map<Long, String> teamGroupMap = teamRegistrationRepository.findTeamGroupMapByTournamentId(tournamentId);
//
//        // Categorize matches
//        for (CricketMatch match : allMatches) {
//            String groupA = teamGroupMap.get(match.getTeamA().getTeamId());
//            String groupB = teamGroupMap.get(match.getTeamB().getTeamId());
//
//            MatchDetailsDTO matchDetail = convertToMatchDetailsDTO(match);
//
//            // Assume teams are in "Group A" or "Group B"
//            String groupKeyA = "Group A".equals(groupA) ? "Group A" : "Group B";
//            String groupKeyB = "Group A".equals(groupB) ? "Group A" : "Group B";
//
//            // Determine the match type
//            String matchType = match.getMatchType().toLowerCase(); // Convert to lower case to match map keys
//
//            if (result.containsKey(matchType)) {
//                result.get(matchType).computeIfAbsent(groupKeyA, k -> new ArrayList<>()).add(matchDetail);
//                if (!groupKeyA.equals(groupKeyB)) { // Avoid adding duplicate matches if groups are the same
//                    result.get(matchType).computeIfAbsent(groupKeyB, k -> new ArrayList<>()).add(matchDetail);
//                }
//            }
//        }
//
//        return result;
//
//    }


    public Map<String, List<CricketMatch>> getMatchesByTypeAndGroup(Long tournamentId) {
        // Assuming you have a method to get matches by tournament ID
        List<CricketMatch> allMatches = cricketMatchRepository.findMatchesByTournamentId(tournamentId);

        // Filter matches based on type and group
        Map<String, List<CricketMatch>> result = new HashMap<>();
        result.put("Group A", new ArrayList<>());
        result.put("Group B", new ArrayList<>());

        for (CricketMatch match : allMatches) {
            if ("Group A".equals(match.getMatchGroup())) {
                result.get("Group A").add(match);
            } else if ("Group B".equals(match.getMatchGroup())) {
                result.get("Group B").add(match);
            }
        }

        return result;
    }
    public List<CricketMatch> findMatchesByTournament(Long tournamentId) {
        TypedQuery<CricketMatch> query = entityManager.createQuery(
                "select c from CricketMatch c where c.tournament.id = :tournamentId", CricketMatch.class);
        query.setParameter("tournamentId", tournamentId);
        return query.getResultList();
    }

    private MatchDetailsDTO convertToMatchDetailsDTO(CricketMatch match) {
        MatchDetailsDTO matchDetail = new MatchDetailsDTO();
        matchDetail.setMatchId(match.getId());
        matchDetail.setTeamA(match.getTeamA().getName());
        matchDetail.setTeamB(match.getTeamB().getName());
        matchDetail.setMatchDateTime(match.getMatchDateTime());
        matchDetail.setLocation(match.getLocation().getCountry() + " - " + match.getLocation().getGround());
        matchDetail.setMatchType(match.getMatchType()); // t20 , ODI , test , IPL

        // Set the match stage (e.g., Playoffs, Semifinals, Finals)
        matchDetail.setMatchStage(match.getMatchStage());

        matchDetail.setMatchGroup(match.getMatchGroup()); // group A or group B
        return matchDetail;
    }

    /**
     * this is for the scheduling Group stage matches by the groupA and groupB and it will save it in the DB
     *
     *
     */


    @Transactional
    public List<MatchDetailsDTO> scheduleGroupStageMatches(Long tournamentId) {
        // Fetch the tournament
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Fetch teams for Group A and Group B
        List<TeamRegistration> groupARegistrations = teamRegistrationRepository.findTeamRegistrationByTournamentIdAndGroupType(tournamentId, "Group A");
        List<TeamRegistration> groupBRegistrations = teamRegistrationRepository.findTeamRegistrationByTournamentIdAndGroupType(tournamentId, "Group B");

        List<Team> groupATeams = groupARegistrations.stream().map(TeamRegistration::getTeam).collect(Collectors.toList());
        List<Team> groupBTeams = groupBRegistrations.stream().map(TeamRegistration::getTeam).collect(Collectors.toList());

        // Fetch all locations
        List<Location> allLocations = locationRepository.findAll();

        if (allLocations.isEmpty()) {
            throw new RuntimeException("No locations available for scheduling matches.");
        }

        // Initialize a list to keep track of used location IDs
        Set<Long> usedLocationIds = new HashSet<>();

        LocalDateTime matchDateTime = tournament.getStartDate();
        List<MatchDetailsDTO> matchDetails = new ArrayList<>();

        // Schedule matches for Group A
        matchDetails.addAll(scheduleMatchesForGroup(tournament, groupATeams, "Group A", matchDateTime, allLocations, usedLocationIds));

        // Update matchDateTime to ensure Group B matches start after Group A matches
        matchDateTime = matchDateTime.plusDays(1);

        // Schedule matches for Group B
        matchDetails.addAll(scheduleMatchesForGroup(tournament, groupBTeams, "Group B", matchDateTime, allLocations, usedLocationIds));

        // Update tournament status
        tournament.setStatus(TournamentStatus.ONGOING);
        tournamentRepository.save(tournament);

        return matchDetails;
    }

    private List<MatchDetailsDTO> scheduleMatchesForGroup(Tournament tournament, List<Team> teams, String group, LocalDateTime matchDateTime, List<Location> allLocations, Set<Long> usedLocationIds) {
        List<MatchDetailsDTO> matchDetailsList = new ArrayList<>();

        int numberOfTeams = teams.size();

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
                match.setMatchStage(MatchStage.PLAYOFF);
                match.setMatchGroup(group);

                cricketMatchRepository.save(match);

                // Create MatchDetailsDTO
                MatchDetailsDTO matchDetail = new MatchDetailsDTO();
                matchDetail.setMatchId(match.getId());
                matchDetail.setTeamA(teamA.getName());
                matchDetail.setTeamB(teamB.getName());
                matchDetail.setMatchDateTime(matchDateTime);
                matchDetail.setLocation(location.getCountry() + " - " + location.getGround());
                matchDetail.setMatchType(tournament.getTournamentName());
                matchDetail.setMatchGroup(group);
                matchDetail.setMatchStage(MatchStage.PLAYOFF);
                matchDetail.setLive(false); // Set to false by default

                matchDetailsList.add(matchDetail);

                // Move to the next match date
                matchDateTime = matchDateTime.plus(tournament.getMatchInterval());
            }
        }

        return matchDetailsList;
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

}
