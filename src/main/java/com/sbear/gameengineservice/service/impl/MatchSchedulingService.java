package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.*;
import com.sbear.gameengineservice.entity.constants.MatchConstants;
import com.sbear.gameengineservice.entity.constants.MatchStage;
import com.sbear.gameengineservice.entity.constants.TournamentStatus;
import com.sbear.gameengineservice.entity.stats.TeamStats;

import com.sbear.gameengineservice.repository.*;
import com.sbear.gameengineservice.repository.stats.TeamStatsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchSchedulingService {


    @Autowired
    private CricketMatchRepository cricketMatchRepository;

    @Autowired
    private TeamRegistrationRepository teamRegistrationRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private TeamStatsRepository teamStatsRepository;
    @Autowired
    private TeamRepository teamRepository;



    public Map<String, List<MatchDetailsDTO>> getMatchesByTypeAndGroup(Long tournamentId) {
        // Assuming you have a method to get matches by tournament ID
        List<CricketMatch> allMatches = cricketMatchRepository.findMatchesByTournamentId(tournamentId);

        // Filter and convert matches based on type and group
        Map<String, List<MatchDetailsDTO>> result = new HashMap<>();
        result.put("Group A", new ArrayList<>());
        result.put("Group B", new ArrayList<>());

        for (CricketMatch match : allMatches) {
            MatchDetailsDTO matchDetail = convertToMatchDetailsDTO(match);
            if ("Group A".equals(match.getMatchGroup())) {
                result.get("Group A").add(matchDetail);
            } else if ("Group B".equals(match.getMatchGroup())) {
                result.get("Group B").add(matchDetail);
            }
        }

        return result;
    }

    private MatchDetailsDTO convertToMatchDetailsDTO(CricketMatch match) {
        MatchDetailsDTO matchDetail = new MatchDetailsDTO();
        matchDetail.setMatchId(match.getId());
        matchDetail.setTeamA(match.getTeamA().getName());
        matchDetail.setTeamB(match.getTeamB().getName());
        matchDetail.setMatchDateTime(match.getMatchDateTime());
        matchDetail.setLocation(match.getLocation().getCountry() + " - " + match.getLocation().getGround());
        matchDetail.setMatchType(String.valueOf(match.getMatchType())); // t20 , ODI , test , IPL

        // Set the match stage (e.g., Playoffs, Semifinals, Finals)
        matchDetail.setMatchStage(match.getMatchStage());
//        matchDetail.setMatchStatus("TournamentStatus.PLANNED.name()"); // this planned
        matchDetail.setMatchStatus(match.getMatchStatus());
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

        if (cricketMatchRepository.existsByTournamentIdAndMatchStage(tournamentId, "PLAYOFF")) {
            throw new RuntimeException("Playoff matches are already scheduled for this tournament.");
        }


        // Fetch teams for Group A and Group B
        List<TeamRegistration> groupARegistrations = teamRegistrationRepository.findTeamRegistrationByTournamentIdAndGroupType(tournamentId, "Group A");
        List<TeamRegistration> groupBRegistrations = teamRegistrationRepository.findTeamRegistrationByTournamentIdAndGroupType(tournamentId, "Group B");

        if(groupARegistrations.isEmpty() || groupBRegistrations.isEmpty()) {
            throw new RuntimeException("team registrations is not completed.");
        }

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
                match.setTournament(tournament); // Ensure this line is present
                match.setLocation(location);
                match.setMatchStage(String.valueOf(MatchStage.PLAYOFF));
                match.setMatchStatus(MatchConstants.PLANNED);
                match.setMatchGroup(group);

                System.out.println("Saving match with Tournament: " + match.getTournament());

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
                matchDetail.setMatchStage(String.valueOf(MatchStage.PLAYOFF));
                matchDetail.setMatchStatus(MatchConstants.PLANNED);
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


    /// schedule the semifinal matches

//    public List<MatchDetailsDTO> scheduleSemiFinals(){
//        List<MatchDetailsDTO> matchDetailsList = new ArrayList<>();
//        List<TeamStats> groupAStats = teamStatsRepository.findTeamStatsByMatchGroup("Group A");
//        List<TeamStats> groupBStats = teamStatsRepository.findTeamStatsByMatchGroup("Group B");
//        // Create maps to aggregate points for each team
//        Map<String, Integer> groupAPointsMap = new HashMap<>();
//        Map<String, Integer> groupBPointsMap = new HashMap<>();
//        // Aggregate points for Group A
//        for (TeamStats stats : groupAStats) {
//            String teamName = stats.getTeamName();
//            Integer currentPoints = groupAPointsMap.getOrDefault(teamName, 0);
//            groupAPointsMap.put(teamName, currentPoints + stats.getPoints());
//        }
//
//        // Aggregate points for Group B
//        for (TeamStats stats : groupBStats) {
//            String teamName = stats.getTeamName();
//            Integer currentPoints = groupBPointsMap.getOrDefault(teamName, 0);
//            groupBPointsMap.put(teamName, currentPoints + stats.getPoints());
//        }
//
//        // Convert maps to lists
//        List<Map.Entry<String, Integer>> groupAList = new ArrayList<>(groupAPointsMap.entrySet());
//        List<Map.Entry<String, Integer>> groupBList = new ArrayList<>(groupBPointsMap.entrySet());
//
//        // Sort lists based on points in descending order
//        groupAList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
//        groupBList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
//
//
//        // Get top 2 teams from each group
//        List<String> topTeamsGroupA = groupAList.stream().limit(1).map(Map.Entry::getKey).collect(Collectors.toList());
//        List<String> topTeamsGroupB = groupBList.stream().limit(1).map(Map.Entry::getKey).collect(Collectors.toList());
//
//        // Retrieve full TeamStats for these top teams
//        List<TeamStats> topTeamStatsGroupA = groupAStats.stream()
//                .filter(stats -> topTeamsGroupA.contains(stats.getTeamName()))
//                .collect(Collectors.toList());
//
//        List<TeamStats> topTeamStatsGroupB = groupBStats.stream()
//                .filter(stats -> topTeamsGroupB.contains(stats.getTeamName()))
//                .collect(Collectors.toList());
//
//        // Convert to MatchDetailsDTO
//        // Assuming MatchDetailsDTO has a constructor or setters to populate its fields
//        for (TeamStats stats : topTeamStatsGroupA) {
//            matchDetailsList.add(new MatchDetailsDTO(
//                    stats.getMatchId(),
//                    stats.getTeamName(),
//                    stats.getPoints(),
//                    stats.getLocation(),
//                    stats.getMatchType(),
//                    "SemiFinals",
//                    stats.getMatchGroup(),
//                    "ONGOING"
//            ));
//        }
//
//        for (TeamStats stats : topTeamStatsGroupB) {
//            matchDetailsList.add(new MatchDetailsDTO(
//                    stats.getMatchId(),
//                    stats.getTeamName(),
//                    stats.getPoints(),
//                    stats.getLocation(),
//                    stats.getMatchType(),
//                    "SemiFinals",
//                    stats.getMatchGroup(),
//                    "ONGOING"
//            ));
//        }
//
//        // Return the sorted list of match details
//        return matchDetailsList;
//
//    }


//    public List<MatchDetailsDTO> scheduleSemiFinals(Long tournamentId) {
//        List<MatchDetailsDTO> matchDetailsList = new ArrayList<>();
//
//        // Fetch the team stats for both groups
//        List<TeamStats> groupAStats = teamStatsRepository.findTeamStatsByMatchGroup("Group A");
//        List<TeamStats> groupBStats = teamStatsRepository.findTeamStatsByMatchGroup("Group B");
//
//        // Create maps to aggregate points for each team
//        Map<String, Integer> groupAPointsMap = new HashMap<>();
//        Map<String, Integer> groupBPointsMap = new HashMap<>();
//
//        // Aggregate points for Group A
//        for (TeamStats stats : groupAStats) {
//            String teamName = stats.getTeamName();
//            Integer currentPoints = groupAPointsMap.getOrDefault(teamName, 0);
//            groupAPointsMap.put(teamName, currentPoints + stats.getPoints());
//        }
//
//        // Aggregate points for Group B
//        for (TeamStats stats : groupBStats) {
//            String teamName = stats.getTeamName();
//            Integer currentPoints = groupBPointsMap.getOrDefault(teamName, 0);
//            groupBPointsMap.put(teamName, currentPoints + stats.getPoints());
//        }
//
//        // Convert maps to lists and sort based on points in descending order
//        List<Map.Entry<String, Integer>> groupAList = new ArrayList<>(groupAPointsMap.entrySet());
//        List<Map.Entry<String, Integer>> groupBList = new ArrayList<>(groupBPointsMap.entrySet());
//        groupAList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
//        groupBList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
//
//        // Get top 2 teams from each group
//        List<String> topTeamsGroupA = groupAList.stream().limit(2).map(Map.Entry::getKey).collect(Collectors.toList());
//        List<String> topTeamsGroupB = groupBList.stream().limit(2).map(Map.Entry::getKey).collect(Collectors.toList());
//        Location location = new Location();
//        location.setId(1);
//        location.setCountry("Australia");
//        location.setGround("Melbourne Cricket Ground");
//        List<CricketMatch> cricketMatches = new ArrayList<>();
//        // Create matches between top teams of Group A and Group B
//        if (topTeamsGroupA.size() == 2 && topTeamsGroupB.size() == 2) {
//            // Create matches for all combinations of top teams from Group A and Group B
//            for (int i = 0; i < topTeamsGroupA.size()-1; i++) {
//                for (int j = 0; j < topTeamsGroupB.size()-1; j++) {
//                    String teamA = topTeamsGroupA.get(i);
//                    String teamB = topTeamsGroupB.get(j);
//
//                    CricketMatch match = new CricketMatch();
//                    Team teamObjA = teamRepository.findTeamByName(teamA);
//                    Team teamObjB = teamRepository.findTeamByName(teamB);
//                    match.setMatchGroup("Knock out Stages");
//                    match.setLocation(location);
//                    match.setTeamA(teamObjA);
//                    match.setTeamB(teamObjB);
//                    match.setLive(false);
//                    match.setMatchStage(String.valueOf(MatchStage.SEMIFINAL));
//                    Tournament tournament = tournamentRepository.findById(tournamentId).get();
//                    match.setTournament(tournament);
//                    match.setMatchStatus(TournamentStatus.ONGOING.name());
//                    cricketMatches.add(match);
//
//                    cricketMatchRepository.save(match);
//
//                }
//            }
//        }
//
//
//        for(CricketMatch match : cricketMatches) {
//            matchDetailsList.add(convertToMatchDetailsDTO(match));
//        }
//
//        // Return the list of match details
//        return matchDetailsList;
//    }


    public List<MatchDetailsDTO> scheduleSemiFinals(Long tournamentId) {
        List<MatchDetailsDTO> matchDetailsList = new ArrayList<>();

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (cricketMatchRepository.existsByTournamentIdAndMatchStage(tournamentId, "Knock out Stages")) {
            throw new RuntimeException("Playoff matches are already scheduled for this tournament.");
        }

        // Check if all group stage matches are completed
        long  areGroupStageMatchesCompleted = cricketMatchRepository.countByTournamentIdAndMatchStageAndMatchStatus(tournamentId,MatchConstants.PLAY_OFF,MatchConstants.PLANNED);
        if (areGroupStageMatchesCompleted>0) {
            throw new RuntimeException("Group stage matches are not yet completed.");
        }

        // Fetch the team stats for both groups
        List<TeamStats> groupAStats = teamStatsRepository.findTeamStatsByMatchGroup("Group A");
        List<TeamStats> groupBStats = teamStatsRepository.findTeamStatsByMatchGroup("Group B");

        // Create maps to aggregate points for each team
        Map<String, Integer> groupAPointsMap = new HashMap<>();
        Map<String, Integer> groupBPointsMap = new HashMap<>();

        // Aggregate points for Group A
        for (TeamStats stats : groupAStats) {
            String teamName = stats.getTeamName();
            Integer currentPoints = groupAPointsMap.getOrDefault(teamName, 0);
            groupAPointsMap.put(teamName, currentPoints + stats.getPoints());
        }

        // Aggregate points for Group B
        for (TeamStats stats : groupBStats) {
            String teamName = stats.getTeamName();
            Integer currentPoints = groupBPointsMap.getOrDefault(teamName, 0);
            groupBPointsMap.put(teamName, currentPoints + stats.getPoints());
        }

        // Convert maps to lists and sort based on points in descending order
        List<Map.Entry<String, Integer>> groupAList = new ArrayList<>(groupAPointsMap.entrySet());
        List<Map.Entry<String, Integer>> groupBList = new ArrayList<>(groupBPointsMap.entrySet());
        groupAList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        groupBList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Get top 2 teams from each group
        List<String> topTeamsGroupA = groupAList.stream().limit(2).map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> topTeamsGroupB = groupBList.stream().limit(2).map(Map.Entry::getKey).collect(Collectors.toList());


        List<Location> allLocations = locationRepository.findAll();
        Set<Long> usedLocationIds = new HashSet<>();
        Location availableLocation = findUnusedLocation(allLocations, usedLocationIds);
        if (availableLocation == null) {
            throw new RuntimeException("Not enough unique locations available for all matches.");
        }
        usedLocationIds.add(availableLocation.getId());


//        // Set up location
//        Location location = new Location();
//        location.setId(1);
//        location.setCountry("Australia");
//        location.setGround("Melbourne Cricket Ground");



        // List to store the matches to be created
        List<CricketMatch> cricketMatches = new ArrayList<>();

        // Create matches between top teams of Group A and Group B
        if (topTeamsGroupA.size() == 2 && topTeamsGroupB.size() == 2) {
            createMatch(topTeamsGroupA.get(0), topTeamsGroupB.get(0), availableLocation, tournamentId, cricketMatches,tournament.getTournamentName(),tournament.getMatchInterval());
            createMatch(topTeamsGroupA.get(1), topTeamsGroupB.get(1), availableLocation, tournamentId, cricketMatches,tournament.getTournamentName(),tournament.getMatchInterval());
        }

        // Save matches and convert to DTO
        for (CricketMatch match : cricketMatches) {
            cricketMatchRepository.save(match);
            matchDetailsList.add(convertToMatchDetailsDTO(match));
        }

        // Return the list of match details
        return matchDetailsList;
    }

    private boolean checkGroupStageMatchesCompleted(Long tournamentId) {
        // Check if all matches in Group A and Group B are completed
        boolean groupACompleted = cricketMatchRepository.allMatchesInGroupCompletedForStage(tournamentId, "Group A", "Completed");
        boolean groupBCompleted = cricketMatchRepository.allMatchesInGroupCompletedForStage(tournamentId, "Group B", "Completed");

        return groupACompleted && groupBCompleted;
    }


    private void createMatch(String teamAName, String teamBName, Location location, Long tournamentId, List<CricketMatch> cricketMatches, String tournamentName , Duration duration) {
        LocalDateTime matchDateTime = LocalDateTime.now();
        CricketMatch match = new CricketMatch();
        Random random = new Random();
        Team teamObjA = teamRepository.findTeamByName(teamAName);
        Team teamObjB = teamRepository.findTeamByName(teamBName);
        match.setMatchGroup(MatchConstants.KNOCK_OUT_STAGES);
        match.setMatchDateTime(matchDateTime.plus(duration).plusHours(4 + random.nextInt(3)));
        match.setMatchType(tournamentName);
        match.setLocation(location);
        match.setTeamA(teamObjA);
        match.setTeamB(teamObjB);
        match.setLive(false);
        match.setMatchStage(String.valueOf(MatchStage.SEMIFINAL));
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        match.setTournament(tournament);
        match.setMatchStatus(TournamentStatus.PLANNED.name());
        cricketMatches.add(match);
    }


    public MatchDetailsDTO getFinalScheduleMatches(Long tournamentId)  {
        List<TeamStats> teamStats = teamStatsRepository.findTop2TeamsByMatchGroupAndPoints("Knock out Stages");
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        LocalDateTime matchDateTime = LocalDateTime.now();
        CricketMatch match = new CricketMatch();
        Random random = new Random();
        Team teamObjA = teamRepository.findTeamByName(teamStats.get(0).getTeamName());
        Team teamObjB = teamRepository.findTeamByName(teamStats.get(1).getTeamName());
        match.setMatchGroup(MatchConstants.FINAL);
        match.setMatchDateTime(matchDateTime.plusHours(4 + random.nextInt(3)));
        assert tournament != null;
        match.setMatchType(tournament.getTournamentName());
        List<Location> allLocations = locationRepository.findAll();
        Set<Long> usedLocationIds = new HashSet<>();
        Location availableLocation = findUnusedLocation(allLocations, usedLocationIds);
        if (availableLocation == null) {
            throw new RuntimeException("Not enough unique locations available for all matches.");
        }
        usedLocationIds.add(availableLocation.getId());
//        Location location = new Location(1,"Australia","Melbourne Cricket Ground");
        match.setLocation(availableLocation);
        match.setTeamA(teamObjA);
        match.setTeamB(teamObjB);
        match.setLive(false);
        match.setMatchStage(String.valueOf(MatchStage.FINAL));
        match.setTournament(tournament);
        match.setMatchStatus(MatchConstants.PLANNED);
        cricketMatchRepository.save(match);

        return convertToMatchDetailsDTO(match);
    }



}
