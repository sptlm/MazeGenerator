package academy.cli;

import academy.solver.Solver;
import academy.solver.impl.AStarSolver;
import academy.solver.impl.BiDirectionalWeightedSolver;
import academy.solver.impl.DijkstraSolver;

public enum SolverNames {
    ASTAR("astar", new AStarSolver()),
    DIJKSTRA("dijkstra", new DijkstraSolver()),
    BIDIRECTIONAL("bidirectional", new BiDirectionalWeightedSolver());

    private String name;
    private Solver solver;

    SolverNames(String name, Solver solver) {
        this.name = name;
        this.solver = solver;
    }

    public String getName() {
        return name;
    }

    public Solver getSolver() {
        return solver;
    }
}
