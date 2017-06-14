import com.sun.corba.se.impl.orbutil.graph.GraphImpl;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

/**
 * Created by chait on 13/06/2017.
 */
public class NLProcessor {
    private  FileUtil f = new FileUtil();
    private Map<String,Set<String>> nerMap = new HashMap();
    Properties props = new Properties();
    StanfordCoreNLP pipeline;

    public void initiateCoreNlp(){
        props.setProperty("annotators", "tokenize, ssplit,pos,lemma,ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);
    }

    public void startSystem(){
        System.out.println("Initiating coreNLP Lib...");
        initiateCoreNlp();
        String path = "C:\\Users\\chait\\Desktop\\article.txt";
        System.out.println("Reading input file at location:\n"+path);
        String line = f.readFromFile(path);//change file path here.
        parse(line);
        System.out.println("System stared....\n\nAsk questions,\nEnter 'quit' to exit");
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String questionInPut =  scanner.nextLine();
            if(!questionInPut.equalsIgnoreCase("quit")){
                //System.out.println(questionInPut);
                String answer = extract(questionInPut);
                if(answer != null){
                    System.out.println("Ans : "+answer);
                }
                else{
                    System.out.println("I Can't fetch answer for this question");
                }
            }
            else{
                break;
            }
        }
    }

    public void parse(String story){
        if (story != null && story.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(story);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                //Tokenization
               // System.out.println(sentence);
                List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);


                for(CoreLabel token:tokens){
                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    if(!ne.equalsIgnoreCase("O")){
                        Set<String> nerList = nerMap.get(ne);
                        String[] t = token.toString().split("-");
                        if(nerList == null){
                           nerList = new HashSet<>();
                        }
                        nerList.add(t[0]);
                        nerMap.put(ne,nerList);
                    }
                }
                // this is the parse tree of the current sentence
                Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
               //System.out.println("parse tree:\n" + tree);
                SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
                //System.out.println("dependency graph:\n" + dependencies);
                // this is the Stanford dependency graph of the current sentence
                /*SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
                System.out.println("dependency graph:\n" + dependencies);*/


                // This is the coreference link graph
                // Each chain stores a set of mentions that link to each other,
                // along with a method for getting the most representative mention
                // Both sentence and token offsets start at 1!
                /*Map<Integer, CorefChain> graph =
                        annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);

                System.out.println(graph);*/

            }
            System.out.println(nerMap);
        }
    }

    public String extract(String question){
        String answer = null;

        parseQuestion(question);

        return answer;
    }

    public String parseQuestion(String question) {
        String ans = null;
        if (question != null && question.length() > 0) {
            Annotation annotation = pipeline.process(question);
            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            for(CoreMap sentence : sentences){
                List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
                String questions[] = question.split(" ");
                String questionType = questions[0];
                String verb = questions[1];
                String obj = questions[2];
                String ne = tokens.get(2).get(CoreAnnotations.NamedEntityTagAnnotation.class);
                switch (questionType){
                    case "who"   : ans = whoType(verb,obj,ne);
                        break;
                    case "where" : ans = whereType(verb,obj,ne);
                        break;
                    case "when"  : ans = whenType(verb,obj,ne);
                        break;
                }
            }
        }
        return ans;
    }

    public String whoType(String verb, String obj,String ne){
        System.out.println("Who type question : "+ne+", is object entity" );
        return null;
    }

    public String whereType(String verb, String obj,String ne){
        System.out.println("Where type question : "+ne+", is object entity" );
        return null;
    }
    public String whenType(String verb, String obj,String ne){
        System.out.println("When type question : "+ne+", is object entity" );
        return null;
    }

}


/*
String fileType = null;
f.writeInTOFile(" C:\\Users\\chait\\Desktop\\KDM\\Week 1B\\Assignment 1B Question types\\"+fileType+".txt");//change output file path here*/
