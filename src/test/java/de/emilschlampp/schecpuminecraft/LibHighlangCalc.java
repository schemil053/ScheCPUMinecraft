package de.emilschlampp.schecpuminecraft;

import de.emilschlampp.scheCPU.high.HighProgramCompiler;
import de.emilschlampp.scheCPU.high.processor.CompileContext;
import de.emilschlampp.scheCPU.util.OCompileProcessor;

import java.util.List;
import java.util.Scanner;

public class LibHighlangCalc {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(LibHighlangCalc.class.getResourceAsStream("/lib/redstone.highlang"));

        String l = "";

        while (scanner.hasNextLine()) {
            l+= scanner.nextLine()+"\n";
        }

        if(l.endsWith("\n")) {
            l = l.substring(0, l.length()-1);
        }

        HighProgramCompiler compiler = new HighProgramCompiler(l).setCompileProcessor(new OCompileProcessor() {
            @Override
            public String generateAfterCompileSCHESEM(CompileContext compileContext) {
                int h = 0;
                for (Integer value : compileContext.getVariableAddresses().values()) {
                    if(value > h) {
                        h = value;
                    }
                }
                h++;
                h-=25;
                System.out.println("h="+h);
                return super.generateAfterCompileSCHESEM(compileContext);
            }
        });
        compiler.compile();
    }
}
