import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class BaseballElimination {

    // Win, Loose, Remaining columns count
    private static final int WLR_COUNT = 3;

    private int[][] games = null;
    private String[] teams = null;

    private String[] teamsBase = null;

    private String lastTeam = null;
    private FordFulkerson lastFordFulkerson = null;

    // create a baseball division from given filename
    public BaseballElimination(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException();
        }
        readTeams(filename);
    }

    private void readTeams(String filename) {
        In in = null;
        try {
            in = new In(filename);
            int countTeams = Integer.parseInt(in.readLine());
            games = new int[countTeams][countTeams + WLR_COUNT];
            teams = new String[countTeams];
            teamsBase = new String[countTeams];
            int teamIdx = 0;
            while (in.hasNextLine()) {
                String currentLine = in.readLine();
                String[] items = currentLine.trim().split("\\s+");
                teams[teamIdx] = items[0];
                teamsBase[teamIdx] = items[0];
                for (int i = 0; i < games[0].length; i++) {
                    games[teamIdx][i] = Integer.parseInt(items[i + 1]);
                }
                teamIdx++;
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private FlowNetwork buildFlowNetwork() {

        // Initial fake node + countGamePairs + teams count (except last) + final fake node
        int vertices = 2 + countGamePairs() + teams.length - 1;
        FlowNetwork flowNetwork = new FlowNetwork(vertices);

        // 0 is used for the initial S node; vertices - 1 is used for the result T node;
        int networkVertexIdx = 1;
        for (int teamIdx = 0; teamIdx < teams.length - 1; teamIdx++) {
            int teamGraphIdx = getTeamGraphIdx(teamIdx);

            for (int gameColumn = WLR_COUNT + 1 + teamIdx; gameColumn < games[0].length - 1; gameColumn++) {

                // Add edge from 0 to the game-vertex (between teams: teamIdx vs column team)
                FlowEdge gameEdge = new FlowEdge(0, networkVertexIdx, games[teamIdx][gameColumn]);
                flowNetwork.addEdge(gameEdge);

                // Add edge from the game-vertex to the teamIdx-vertex
                FlowEdge winsEdge1 = new FlowEdge(networkVertexIdx, teamGraphIdx, Double.POSITIVE_INFINITY);
                flowNetwork.addEdge(winsEdge1);

                // Add edge from game-vertex to the column-team-vertex
                int oppositeTeamGraphIdx = getOppositeTeamGraphIdx(gameColumn);
                FlowEdge winsEdge2 = new FlowEdge(networkVertexIdx, oppositeTeamGraphIdx, Double.POSITIVE_INFINITY);
                flowNetwork.addEdge(winsEdge2);

                networkVertexIdx++;
            }

            // Rely on last string in the table is team for elimination check
            // Add edge from the current teamIdx-team to the final t-vertex (in the terms of the s-t flow)
            int capacity = games[games.length - 1][0] + games[games.length - 1][2] - games[teamIdx][0];
            if (capacity < 0)
                capacity = 0;
            FlowEdge remainingWinsEdge = new FlowEdge(teamGraphIdx, vertices - 1, capacity);
            flowNetwork.addEdge(remainingWinsEdge);
        }
        return flowNetwork;
    }

    private void swapTeams(String team) {

        int teamRowIdx = teamIdx(team);
        int teamColumnIdx = WLR_COUNT + teamRowIdx;

        int columns = games[0].length;
        int rows = teams.length;

        // Swapping rows
        for (int column = 0; column < columns; column++) {
            int temp = games[teamRowIdx][column];
            games[teamRowIdx][column] = games[teams.length - 1][column];
            games[teams.length - 1][column] = temp;
        }

        // Swapping columns
        for (int row = 0; row < rows; row++) {
            int temp = games[row][teamColumnIdx];
            games[row][teamColumnIdx] = games[row][columns - 1];
            games[row][columns - 1] = temp;
        }

        String tempTeam = teams[teamRowIdx];
        teams[teamRowIdx] = teams[teams.length - 1];
        teams[teams.length - 1] = tempTeam;
    }

    private int getOppositeTeamGraphIdx(int gameColumnIdx) {
        return 1 + countGamePairs() + (gameColumnIdx - WLR_COUNT);
    }

    private int getTeamGraphIdx(int teamIdx) {
        return 1 + countGamePairs() + teamIdx;
    }

    private int getTeamIdxFromGraphIdx(int graphIdx) {
        return graphIdx - 1 - countGamePairs();
    }

    private int countGamePairs() {
        int n = teams.length - 1;

        // Use arithmetical progression to calculate number of game pairs, result of the ((N-1) + 1)/2 * (N-1)
        return (n - 1) * n / 2;
    }

    // number of teams
    public int numberOfTeams() {
        return games.length;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(teamsBase);
    }

    // number of wins for given team
    public int wins(String team) {
        validateTeam(team);
        return games[teamIdx(team)][0];
    }

    // number of losses for given team
    public int losses(String team) {
        validateTeam(team);
        return games[teamIdx(team)][1];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        validateTeam(team);
        return games[teamIdx(team)][2];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        return games[teamIdx(team1)][WLR_COUNT + teamIdx(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        validateTeam(team);
        return certificateOfElimination(team) != null;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        Iterable<String> eliminations = mathCertificateOfElimination(team);
        if (eliminations != null) {
            return eliminations;
        }
        FordFulkerson fordFulkerson = findMaxFlow(team);
        return findCertificateOfElimination(fordFulkerson);
    }

    private FordFulkerson findMaxFlow(String team) {
        if (team.equals(lastTeam)) {
            return lastFordFulkerson;
        }
        lastTeam = team;
        swapTeams(team);
        FlowNetwork flowNetwork = buildFlowNetwork();
        lastFordFulkerson = new FordFulkerson(flowNetwork, 0, flowNetwork.V() - 1);
        return lastFordFulkerson;
    }

    private Iterable<String> findCertificateOfElimination(FordFulkerson fordFulkerson) {
        List<String> certificateOfElimination = null;
        for (int v = getTeamGraphIdx(0); v < getTeamGraphIdx(teams.length - 1); v++) {
            if (fordFulkerson.inCut(v)) {
                if (certificateOfElimination == null) {
                    certificateOfElimination = new ArrayList<>();
                }
                certificateOfElimination.add(teams[getTeamIdxFromGraphIdx(v)]);
            }
        }
        return certificateOfElimination;
    }

    private int teamIdx(String team) {
        for (int i = 0; i < teams.length; i++) {
            if (teams[i].equals(team)) {
                return i;
            }
        }
        return -1;
    }

    private Iterable<String> mathCertificateOfElimination(String team) {
        List<String> certificateOfElimination = null;
        int teamIdx = teamIdx(team);
        for (int i = 0; i < teams.length; i++) {
            if (i != teamIdx) {
                if (games[teamIdx][0] + games[teamIdx][2] < games[i][0]) {
                    if (certificateOfElimination == null) {
                        certificateOfElimination = new ArrayList<>();
                    }
                    certificateOfElimination.add(teams[i]);
                }
            }
        }
        return certificateOfElimination;
    }

    private void validateTeam(String team) {
        if (team == null) {
            throw new IllegalArgumentException();
        }
        if (teamIdx(team) == -1) {
            throw new IllegalArgumentException();
        }
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
