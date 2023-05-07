package dk.gtz.graphedit.demo;

import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] argv) {
        var args = new Args();
        var b = JCommander.newBuilder()
            .addObject(args)
            .programName("demo")
            .build();
        b.parse(argv);
        if(args.getHelp()) {
            b.usage();
            return;
        }
        System.out.println("You provided verbosity level %d".formatted(args.getVerbosity()));
    }
}


