package main;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.lang.*;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;

import java.io.*;

public class VSM {

    private static List<String> stopWords = new ArrayList<String>(); // List which will store the stopwords
    public static Map<String, List<Integer>> dictionary = new HashMap<String, List<Integer>>();
    public static Map<String, List<Double>> wtgMap = new HashMap<>();
    public static List<Double> idfFinal = new ArrayList<>();
    public static List<Double> query = new ArrayList<>();
    public static int totaldocument = 56;
    public static Map<Integer,Double> DocID = new HashMap<Integer,Double>();
    public static double alpha = 0.0005;

    //Function it will save the stopwords from the file into the List<String>
    public static void readStopList() throws FileNotFoundException {
        String tempBuffer = "";
        Scanner sc_obj = new Scanner(new File("./inputFiles/Stopword-List.txt"));

        while(sc_obj.hasNext()) {
            tempBuffer = sc_obj.next();
            stopWords.add(tempBuffer);
        }
        sc_obj.close();
    }

    //Function it will read all the data from the 56 trump speeches and it will save into the hashmap <Dictionary> and it will also maintain
    //											the term frequency of word in each document
    public static void readDatafiles() throws FileNotFoundException {
        int docNumber = 0;

        while(docNumber<totaldocument) { // loop will run until all 0 - 55 speech data is read
            String tempBuffer = "";

            Scanner sc_obj = new Scanner(new File("./inputFiles/speech_"+docNumber+".txt"));
            sc_obj.nextLine(); //skip the first line of every document

            while(sc_obj.hasNext()) {
                tempBuffer = sc_obj.next();
                tempBuffer = tempBuffer.replaceAll("[^a-zA-Z]+"," ");

                String[] wordTerm = tempBuffer.split(" |//."); // the read data will convert into single word from whole stream of characters
                // it will split according to white spaces . - , like special characters

                for (int i=0; i < wordTerm.length; i++) {

                    String term = wordTerm[i].toLowerCase();		//each splitted word will be converted into lower case
                    term = RemoveSpecialCharacter(term);			// it will remove all the characters apart from the english letters
                    term = removeStopWords(term);					// it will remove the stopWords and final version of the term in the form of tokens will form

                    if(!term.equalsIgnoreCase("") && term.length()>1) {
                        term = Lemmatize(term);					//all the words in the form of tokens will be lemmatized
                        //increment frequency of word if it is already present in dictionary
                        if(dictionary.containsKey(term)) {		//all the lemmatized words will be placed in HashMap dictionary
                            List<Integer> presentList = dictionary.get(term);
                            int wordFrequency = presentList.get(docNumber);
                            wordFrequency++;
                            presentList.set(docNumber, wordFrequency);		//frequency of all the lemmatized words in dictionary is maintained 									//i.e: Word <2.0,1.0,3.0,0.0 ...> here hashmap<String,List<Double> is used
                        }										//the 0th index shows the word appared 2 times in doc 0 and 1 times in doc 1 and so forth..
                        else { // if word was not in the dictionary then it will be added
                            // if word was found in 5 doc so from 0 to 4 index representing doc 0 to doc 4  (0.0) will be placed
                            List<Integer>newList = new ArrayList<>();
                            for(int j=0; j<57; j++) {
                                if(j != docNumber)
                                    newList.add(0);
                                else
                                    newList.add(1);
                            }
                            dictionary.put(term, newList);
                        }
                    }
                }

            }
            docNumber++;
        }
    }

    // Pre-Processing Functions ***********************************************************************************************************************

    //This function will remove all the specialcharacter associated with the word
    private static String RemoveSpecialCharacter (String s1)  {
        String tempResult="";
        for (int i=0;i<s1.length();i++)  {
            if (s1.charAt(i)>64 && s1.charAt(i)<=122){
                tempResult=tempResult+s1.charAt(i);
            }
        }
        return tempResult;
    }

    //function to remove stop words from the documents
    private static String removeStopWords(String str) {
        for(int i=0; i<stopWords.size(); i++) {
            String temp = stopWords.get(i);
            if(temp.equalsIgnoreCase(str)) {
                str = "";
            }
        }
        return str;
    }

    //the function is only used for lemmatization purpose
    private static String Lemmatize(String strTemp) {
        Properties obj = new Properties();
        obj.setProperty("annotators", "tokenize, ssplit, pos, lemma"); //setting the properties although using only for lemmatization purpose but one cannot
        // removed the tokenize ssplit pos arguments
        StanfordCoreNLP pipeObj = new StanfordCoreNLP(obj);		//using stanFord library and creating its object
        Annotation annotationObj;
        annotationObj = new Annotation(strTemp); //creating annotation object and passing the string word
        pipeObj.annotate(annotationObj);
        String finalLemma = new Sentence(strTemp).lemma(0); //we only using the lemma of the passed string Word rest of the features like pos, ssplit, tokenized are ignored
        //although we can use it but tokenization has been done perviously
        //with my own code
        return finalLemma;
    }


    //function to calculate the Document Frequency of a lemmatized word
    public static void DocumentFrequencies() throws FileNotFoundException {
        for (Entry<String, List<Integer>> entry : dictionary.entrySet()) {
            List<Integer> termfrequency = new ArrayList<>();

            termfrequency = entry.getValue();	// getting each word's termfrequency list
            int i = 0;
            termfrequency.add(0); 			//maintaining documentfrequency at the 57th index
            Integer documentFrequency = termfrequency.get(totaldocument+1);
            while(i<56) {        //iterate 0 to 55 all speeches term frequency
                if(termfrequency.get(i)>0) {		//increment document frequency of that word if termfrequency is greater than 0.0
                    documentFrequency++;			//increment document frequency
                }
                i++;		//increment index
            }
            termfrequency.set(57, documentFrequency);	//re-set the documentfrequency and save the current maintained document frequency from 0 to calculated value
            dictionary.put(entry.getKey(), termfrequency);		// place the incremented final value of that word back to the same list in dictionary hashmap
        }
        //save();
    }

    public static void save() throws FileNotFoundException {
        String fileName = "./outputFiles/dictionaryTermFrequency.txt";
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
        for (Map.Entry<String,List<Integer>> entry : dictionary.entrySet()){
            pw.println("key: " + entry.getKey() + "-> " + entry.getValue());
        }
        pw.close();
    }

    //***************************************************************************************************************************************

    //function it will read the Query entered by the user
    static void ReadQuery(String input) throws FileNotFoundException {
        input = input.replaceAll("[^A-Za-z]"," ");
        String[] arr = input.split(" "); // splitting the whole string into words by split on the basis of white spaces


        for(int i=0; i<arr.length; i++) {
            String termWord = arr[i].toLowerCase();	//same pre-processing is applied to all the query word
            //termWord = RemoveSpecialCharacter(termWord);
            termWord = removeStopWords(termWord);

            if(!termWord.equalsIgnoreCase("")) { // all the white spaces are removed as if not removed then lemmatization wont be successfully done

                termWord = Lemmatize(termWord);
                System.out.println(termWord);
                if(dictionary.containsKey(termWord)) {
                    List<Integer> wordList = new ArrayList<>();
                    wordList = dictionary.get(termWord);
                    int queryWordFrequency = wordList.get(totaldocument);
                    queryWordFrequency++;
                    wordList.set(totaldocument, queryWordFrequency);  // all the frequencies of the query words are stored at the 56th index of the List stored in the
                    //hashmap associated with its word-terms
                    dictionary.put(termWord, wordList);
                }
                else {
                    //if any of the enterd query word not present in all the docs so list will have 0.0 value from 0th index to 55th and 56th index is reserver
                    // for query word frequency
                    List<Integer> wordList = new ArrayList<>();
                    for(int j=0; j<totaldocument+1; j++) {
                        wordList.add(0);
                    }
                    wordList.add(1);
                    dictionary.put(termWord, wordList); //updating the dictionary hashmap now containing all the query words frequencies
                }
            }
        }
        save();
    }
    public static void saveidf() throws FileNotFoundException {
        String fileName = "./outputFiles/idfFinal.txt";
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
        int i = 0;
        for (Map.Entry<String,List<Integer>> entry : dictionary.entrySet()){
            pw.println(entry.getKey() + "-> " + idfFinal.get(i));
            i++;
        }
        pw.close();
    }
    //funtion it will will calculate the idf of with the help of log(documentfrequency)/N .. where N = no. of docs == 56
    public static void InvertedDocFrequency() throws FileNotFoundException {
        //int totalDoc = totaldocument;
        int i = 0;
        for(Entry<String, List<Integer>> entry: dictionary.entrySet()) {
            List<Integer> docFrequency = new ArrayList<>();
            //	List<Double> idfValue = new ArrayList<>();
            docFrequency = entry.getValue();            //again taking the list of word terms from the  same dictionary hashmap
            int df = docFrequency.get(totaldocument+1);            // it will save the idf of every word term at the 57th index
            double ans = Math.log10(df);
            ans /=  totaldocument;

            idfFinal.add(i,ans); //updatin
            i++;
        }
        saveidf();
    }

    //This function will calculated the weights by multiplying the idf * list values from 0 to 56 of every single word
    public static void WeightedVector() throws FileNotFoundException {
        int doc = 0;
        for(Entry<String, List<Integer>> entry: dictionary.entrySet()) {
            List<Integer> wtg = new ArrayList<>();
            List<Double> newList = new ArrayList<>();
            wtg = entry.getValue();
            int i = 0;
            Double mul = 0.0;
            while(i<57) {
                mul = wtg.get(i) * idfFinal.get(doc);
                newList.add(i, mul);
                i++;
            }
            wtgMap.put(entry.getKey(),newList);
            doc++;
        }
        savewtg();
    }

    //writing the tf*idf vector in a file
    public static void savewtg() throws FileNotFoundException {
        String fileName = "./outputFiles/wtgVector.txt";
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
        for (Map.Entry<String,List<Double>> entry : wtgMap.entrySet()){
            pw.println(entry.getKey() + "-> " + entry.getValue());
        }
        pw.close();
    }
    //creating query list having term frequency of query words
    public static void QueryVector() {
        for(Entry<String, List<Double>> entry: wtgMap.entrySet()){
            List<Double> queryValue = new ArrayList();
            queryValue = entry.getValue();
            double temp = queryValue.get(totaldocument);

            query.add(temp);
        }
    }
    //function it will calculate the cosine similarity value taking 2 list doc list and query list
    public static double CosineSimilarity(List<Double> Doc, List<Double> query) {
        double answer = 0 ;
        double sqA = 0;
        double sqB = 0;

        for(int i=0; i<query.size(); i++) {
            answer = answer + (Doc.get(i) * query.get(i));
            sqA = sqA+(Doc.get(i)*Doc.get(i));
            sqB = sqB+(query.get(i)*query.get(i));
        }
        if(sqA==0 &&sqB==0)
            return 2.0;

        return answer / (Math.sqrt(sqA) * Math.sqrt(sqB));
    }
    //it will create 55 doc list
    public static void docWtgArray() {
        int docCount = 0;

        while(docCount<totaldocument) {
            List<Double> pass = new ArrayList();
            for(Entry<String,List<Double>> entry : wtgMap.entrySet()) {
                List<Double> docList = new ArrayList();
                docList = entry.getValue();
                double temp = docList.get(docCount);
                pass.add(temp);
            }

            double answer = CosineSimilarity(pass,query);
            if(answer >= alpha) {
                DocID.put(docCount,answer);
            }
            docCount++;
        }
    }
    //it will sort the answers in reverse order or descending order
    public static String Sorts(Map<Integer,Double> docsInfo) {
        Map<Integer, Double> sorted = docsInfo.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                LinkedHashMap::new));
        String ans = "";
        sorted = docsInfo
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        for(Map.Entry<Integer, Double> en : sorted.entrySet()) {
            ans = ans + ("doc.Id --->  " + en.getKey() +"\n");
        }
        GUI.count = String.valueOf(sorted.size());
        return ans;
    }

    //main driver function
    public static void main(String[] args) throws FileNotFoundException {
        control.main(args); //calling the GUI main function
    }

}