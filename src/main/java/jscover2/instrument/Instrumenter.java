package jscover2.instrument;

public class Instrumenter {
    public String instrument(String js) {
        return "if (!jscover) {\n" +
                "  var jscover = {};\n" +
                "}\n" +
                "if (!jscover['test.js']) {\n" +
                "  jscover['test.js'] = {};\n" +
                "  jscover['test.js'].s = [];\n" +
                "  jscover['test.js'].s[0] = 0;\n" +
                "}\n" +
                "if (! jscover['test.js'].f) {\n" +
                "  jscover['test.js'].f = [];\n" +
                "}\n" +
                "if (!jscover['test.js'].b) {\n" +
                "  jscover['test.js'].b = {};\n" +
                "}\n" +
                "jscover['test.js'].s[0]++;\n" +
                "x = 1;\n";
    }
}
