import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {

    private final int n;
    private final int numberOfGamesExcludingTeam;
    private final Map<String, Integer> teamToId;
    private final Map<Integer, String> idToTeam;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;
    private final Map<String, Iterable<String>> teamToCertificateOfElimination;
    private final Map<String, Integer> teamToFullMaxFlow;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        n = in.readInt();
        numberOfGamesExcludingTeam = (n - 1) * (n - 2) / 2;
        teamToId = new HashMap<>();
        idToTeam = new HashMap<>();
        teamToCertificateOfElimination = new HashMap<>();
        teamToFullMaxFlow = new HashMap<>();
        wins = new int[n];
        losses = new int[n];
        remaining = new int[n];
        games = new int[n][n];

        for (int i = 0; i < n; i++) {
            String team = in.readString();
            teamToId.put(team, i);
            idToTeam.put(i, team);
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < n; j++) {
                games[i][j] = in.readInt();
            }
        }
    }

    public int numberOfTeams() {
        return n;
    }

    public Iterable<String> teams() {
        return new ArrayList<>(teamToId.keySet());
    }

    private void checkTeamIsValid(String team) {
        if (team == null || !teamToId.containsKey(team)) {
            throw new IllegalArgumentException(team + " is not valid!");
        }
    }

    public int wins(String team) {
        checkTeamIsValid(team);
        return wins[teamToId.get(team)];
    }

    public int losses(String team) {
        checkTeamIsValid(team);
        return losses[teamToId.get(team)];
    }

    public int remaining(String team) {
        checkTeamIsValid(team);
        return remaining[teamToId.get(team)];
    }

    public int against(String team1, String team2) {
        checkTeamIsValid(team1);
        checkTeamIsValid(team2);
        return games[teamToId.get(team1)][teamToId.get(team2)];
    }

    private int getTeamVertex(int teamId, int teamToCheckId) {
        return (teamId > teamToCheckId) ? numberOfGamesExcludingTeam + teamId : 1 + numberOfGamesExcludingTeam + teamId;
    }

    public boolean isEliminated(String team) {
        checkTeamIsValid(team);
        return certificateOfElimination(team) != null;
    }

    private int getTrivialCertificate(String team) {
        int teamId = teamToId.get(team);
        for (int i = 0; i < wins.length; i++) {
            if (wins[teamId] + remaining[teamId] < wins[i]) {
                return i;
            }
        }
        return -1;
    }

    private Iterable<String> certificateOfEliminationPrivate(String team) {
        if (n == 1) {
            return new ArrayList<>();
        }

        int trivialCertificateId = getTrivialCertificate(team);

        if (trivialCertificateId != -1) {
            return Collections.singletonList(idToTeam.get(trivialCertificateId));
        }

        if (n == 2) {
            return new ArrayList<>();
        }

        final FlowNetwork flowNetwork = getFlowNetwork(team);
        final FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, numberOfGamesExcludingTeam + n);

        if (fordFulkerson.value() == teamToFullMaxFlow.get(team)) {
            return new ArrayList<>();
        } else {
            List<String> certificateOfElimination = new ArrayList<>();
            int teamToCheckId = teamToId.get(team);
            for (int curTeamId = 0; curTeamId < n; curTeamId++) {
                if (curTeamId == teamToCheckId) {
                    continue;
                }
                int teamVertex = getTeamVertex(curTeamId, teamToCheckId);
                if (fordFulkerson.inCut(teamVertex)) {
                    certificateOfElimination.add(idToTeam.get(curTeamId));
                }
            }
            return certificateOfElimination;
        }

    }

    public Iterable<String> certificateOfElimination(String team) {
        checkTeamIsValid(team);
        final Iterable<String> certificateOfElimination = teamToCertificateOfElimination.computeIfAbsent(team, this::certificateOfEliminationPrivate);
        if (certificateOfElimination.iterator().hasNext()) {
            return certificateOfElimination;
        } else {
            return null;
        }
    }

    private FlowNetwork getFlowNetwork(String teamToCheck) {
        int teamToCheckId = teamToId.get(teamToCheck);
        // total number of vertices = 1 (source) + numberOfGamesExcludingTeam + n - 1 (number of teams excluding single team) + 1 (sink)
        int totalNumberOfVertices = 1 + numberOfGamesExcludingTeam + n;
        final FlowNetwork flowNetwork = new FlowNetwork(totalNumberOfVertices);

        int currentGameVertex = 1;
        for (int curTeamId = 0; curTeamId < n; curTeamId++) {
            if (curTeamId == teamToCheckId) {
                continue;
            }
            for (int opponentTeamId = curTeamId + 1; opponentTeamId < n; opponentTeamId++) {
                if (opponentTeamId == teamToCheckId) {
                    continue;
                }
                // connect source vertex (0) with game vertices and assign edge capacity as games[i][j]
                flowNetwork.addEdge(new FlowEdge(0, currentGameVertex, games[curTeamId][opponentTeamId]));
                teamToFullMaxFlow.merge(teamToCheck, games[curTeamId][opponentTeamId], Integer::sum);
                // two edges from game vertex to possible winners of the game
                int curTeamVertex = getTeamVertex(curTeamId, teamToCheckId);
                int opponentTeamVertex = getTeamVertex(opponentTeamId, teamToCheckId);
                flowNetwork.addEdge(new FlowEdge(currentGameVertex, curTeamVertex, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(new FlowEdge(currentGameVertex, opponentTeamVertex, Double.POSITIVE_INFINITY));
                currentGameVertex++;
            }
        }

        for (int curTeamId = 0; curTeamId < n; curTeamId++) {
            if (curTeamId == teamToCheckId) {
                continue;
            }
            int teamVertex = getTeamVertex(curTeamId, teamToCheckId);
            flowNetwork.addEdge(new FlowEdge(teamVertex, totalNumberOfVertices - 1, wins(teamToCheck) + remaining(teamToCheck) - wins[curTeamId]));
        }

        return flowNetwork;
    }

    @Override
    public String toString() {
        return "BaseballElimination{" +
                "n=" + numberOfTeams() +
                ", teams=" + teams() +
                ", wins=" + Arrays.toString(wins) +
                ", losses=" + Arrays.toString(losses) +
                ", remaining=" + Arrays.toString(remaining) +
                ", games=" + Arrays.deepToString(games) +
                '}';
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
