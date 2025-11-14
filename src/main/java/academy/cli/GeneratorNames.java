package academy.cli;

import academy.generator.Generator;
import academy.generator.impl.CyclicMazeGenerator;
import academy.generator.impl.DfsGenerator;
import academy.generator.impl.PrimGenerator;

public enum GeneratorNames {
    DFS("dfs", new DfsGenerator()),
    PRIM("prim", new PrimGenerator()),
    CYCLIC("cyclic", new CyclicMazeGenerator(new DfsGenerator()));

    private String name;
    private Generator generator;

    GeneratorNames(String name, Generator generator) {
        this.name = name;
        this.generator = generator;
    }

    public String getName() {
        return name;
    }

    public Generator getGenerator() {
        return generator;
    }
}
