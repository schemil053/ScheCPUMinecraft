package de.emilschlampp.schecpuminecraft.compiler;

import de.emilschlampp.scheCPU.compile.Compiler;
import de.emilschlampp.scheCPU.dissassembler.Decompiler;
import de.emilschlampp.scheCPU.high.HighProgramCompiler;
import de.emilschlampp.scheCPU.high.preprocessing.HighLangPreprocessor;
import de.emilschlampp.scheCPU.high.preprocessing.PreprocessorEnvironment;
import de.emilschlampp.scheCPU.high.processor.CompileContext;
import de.emilschlampp.scheCPU.high.processor.CompileProcessor;
import de.emilschlampp.scheCPU.util.OCompileProcessor;
import de.emilschlampp.schecpuminecraft.util.CodeType;
import de.emilschlampp.schecpuminecraft.util.IOFace;

import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;

public class CPUCompiler {
    public static byte[] compile(CodeType type, String code) {
        if(type.equals(CodeType.SCHESSEMBLER)) {
            return new Compiler(code).compile();
        }
        if(type.equals(CodeType.BASE64ASM)) {
            return Base64.getDecoder().decode(code.replace(" ", "").replace("\n", ""));
        }
        if(type.equals(CodeType.HIGHLANG)) {
            return new HighProgramCompiler(new HighLangPreprocessor(code)
                    .setPreprocessorEnvironment(new PreprocessorEnvironment().setFileInclusionWhiteList(true).setFileInclusionWhitelist(Arrays.asList("redstone"))
                            .setFileInputStreamCreator(s -> CPUCompiler.class.getResourceAsStream("/lib/"+s+".highlang")))
                    .preprocess().getResult()).setCompileProcessor(new HighlangMCCompileProcessor()).setWarningOutput(s -> {}).toBytecode(); //TODO 27.09.2024 Warnings
        }

        return null;
    }

    public static String decompile(byte[] code) {
        return new Decompiler(code).decompile();
    }

    private static class HighlangMCCompileProcessor extends OCompileProcessor {

        @Override
        public void startCompile(CompileContext compileContext) {
            compileContext.getOptions().put("io-print-port", "130");
            compileContext.getProtectedOptions().add("io-print-port");
        }

        @Override
        public String generatePreCompileSCHESEM() {
            return "OUTW 129 20\n"+ // speed up, highlang is a bit slow (compared to schessem)
                    "OUTW 128 1"; // enable speed up
        }

        @Override
        public String generateSchesemForInstruction(CompileContext compileContext, String[] instruction) {
            if(instruction[0].equals("redstonemode")) {
                IOFace face = IOFace.valueOf(instruction[1].toUpperCase(Locale.ROOT));

                int mode = Integer.parseInt(instruction[2]); //TODO 28.09.2024 strings als modes

                return "OUTW "+face.getIOConfigID()+" "+mode;
            }
            if(instruction[0].equals("writeredstonemem")) {
                IOFace face = IOFace.valueOf(instruction[1].toUpperCase(Locale.ROOT));

                int address = compileContext.getVariableAddresses().get(instruction[2]);

                return "OUTWM "+face.getIOValueID()+" "+address;
            }
            return null;
        }
    }
}
