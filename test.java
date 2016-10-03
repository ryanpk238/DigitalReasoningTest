/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Ryan Kennedy
 */
public class test 
{

    public static void main(String[] args) 
    {
        test3 myTest = new test3();       
    }
    
}

class test3
{
    private ArrayList<String> alBoundaries = new ArrayList<String>();
    private ArrayList<String> alNames = new ArrayList<String>();
    
    test3()
    {
        alBoundaries.add("\n");
        alBoundaries.add(".\"");
        alBoundaries.add("? ");
        alBoundaries.add(". ");
        alBoundaries.add(". ");
        alBoundaries.add("! ");

        
        //String strInFile = "C:\\test\\input\\nlp_data\\nlp_data\\d10.txt";
                
        //getByWord(strInFile);
        //getNames("C:\\test\\input\\NER.txt");
        //assembleSentences();
        //tagNames();
        //outputToXML();        
        
        
        String strInFile=null;
        String strOutFile=null;
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) 
        {
            if(i == 9)
            {
                strInFile = "C:\\test\\input\\nlp_data\\nlp_data\\d10.txt";
                strOutFile = "C:\\test\\output\\d10.xml";
            }
            else
            {
                strInFile = "C:\\test\\input\\nlp_data\\nlp_data\\d0" + (i+1) + ".txt";
                strOutFile = "C:\\test\\output\\d0" + (i+1) + ".xml";
            }
            Runnable worker = new WorkerThread(strInFile, strOutFile);
            executor.execute(worker);
         }
        executor.shutdown();
        while (!executor.isTerminated()) 
        {
            //System.out.println("processing...");
        }
        System.out.println("Finished all threads");
        
    }

    public class WorkerThread implements Runnable 
    {
        private String strFile;
        private String strOutFile;
        private ArrayList<String> alWords = new ArrayList<String>();
        private ArrayList<String> alSentences = new ArrayList<String>();
    
        public WorkerThread(String s, String out)
        {
            this.strFile=s;
            this.strOutFile=out;
        }

        @Override
        public void run() 
        {
            System.out.println(Thread.currentThread().getName()+" Start. Command = " + strFile);
            processCommand();
            System.out.println(Thread.currentThread().getName() + " End.");
        }

        private void processCommand() 
        {
            try 
            {
                getByWord(this.strFile);
                getNames("C:\\test\\input\\NER.txt");
                assembleSentences();
                tagNames();
                outputToXML(this.strOutFile);            
                Thread.sleep(5000);
            }
            catch (Exception e) 
            {
                System.out.println(e.toString());
            }
        }

        @Override
        public String toString()
        {
            return this.strFile;
        }
        
        public void outputToXML(String strFile)
        {
            try
            {
                int loopCnt=0;

                //strFile = strFile.replace(".txt","_out.xml");

                StringBuilder strXML = new StringBuilder("<?xml version=\"1.0\"?>");
                strXML.append("<object javaVersion=\"1.8.0_92\" class=\"test.test3\">");

                strXML.append("\n");
                strXML.append("<properties>");

                    strXML.append("\n");
                    strXML.append("<property name=\"alBoundaries\" type=\"ArrayList[String]\" description=\"Contains known sentence boundaries from text input\">");
                    loopCnt=alBoundaries.size();
                    for(int i=0;i<loopCnt;i++)
                    {
                        strXML.append("\n");
                        if(alBoundaries.get(i).equals("\n"))
                            strXML.append("<propertyValue index=\"" + i + "\">\\n</propertyValue>");
                        else
                            strXML.append("<propertyValue index=\"" + i + "\">" + alBoundaries.get(i) + "</propertyValue>");
                    }
                    strXML.append("\n");
                    strXML.append("</property>");

                    strXML.append("\n\n");
                    strXML.append("<property name=\"alSentences\" type=\"ArrayList[String]\" description=\"Contains sentences from text input\">");
                    loopCnt=alSentences.size();
                    for(int i=0;i<loopCnt;i++)
                    {
                        strXML.append("\n");
                        strXML.append("<propertyValue index=\"" + i + "\">" + alSentences.get(i) + "</propertyValue>");
                    }
                    strXML.append("\n");
                    strXML.append("</property>");

                    strXML.append("\n\n");
                    strXML.append("<property name=\"alWords\" type=\"ArrayList[String]\" description=\"Contains words from text input\">");            
                    loopCnt=alWords.size();
                    for(int i=0;i<loopCnt;i++)
                    {
                        strXML.append("\n");
                        strXML.append("<propertyValue index=\"" + i + "\">" + alWords.get(i) + "</propertyValue>");
                    }
                    strXML.append("</property>");

                strXML.append("\n");
                strXML.append("</properties>");                

                strXML.append("\n\n");
                strXML.append("<methods>");  
                    strXML.append("\n");
                    strXML.append("<method name=\"Left\" parameters=\"[String],[int]\" description=\"Returns characters from left side of a string\"></method>");

                    strXML.append("\n");
                    strXML.append("<method name=\"Right\" parameters=\"[String],[int]\" description=\"Returns characters from right side of a string\"></method>");

                    strXML.append("\n");
                    strXML.append("<method name=\"assembleSentences()\" parameters=\"\" description=\"Assembles text input words into sentences and stores in ArrayList[String]\"></method>");

                    strXML.append("\n");
                    strXML.append("<method name=\"getByWord\" parameters=\"\" description=\"Gets words from text input and stores in ArrayList[String]\"></method>");

                    strXML.append("\n");
                    strXML.append("<method name=\"isEndWord\" parameters=\"[String]\" description=\"Marks words that fall at the end of a sentence\"></method>");

                    strXML.append("\n");
                    strXML.append("<method name=\"outputToXML\" parameters=\"\" description=\"Outputs object schema in readable XML\"></method>");            

                strXML.append("\n");
                strXML.append("</methods></object>");             


                System.out.println(strXML.toString());
                writeToFile(strXML.toString(), strFile);
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
            }         
        }

        private void assembleSentences()
        {
            int loopCnt = alWords.size();
            String strSentence="";

            for(int i=0;i<loopCnt;i++)
            {
                strSentence += alWords.get(i) + " ";
                if(Right(alWords.get(i),1).equals("|"))
                {
                    if(i+1 < loopCnt)
                    {
                         if(Character.isUpperCase(alWords.get(i+1).charAt(0)))
                         {
                            alSentences.add(strSentence.replace("|",""));
                            strSentence = "";                         
                         }
                    }
                    else if(i+1 == loopCnt)
                    {
                        alSentences.add(strSentence.replace("|",""));
                        strSentence = "";                     
                    }
                }
            }
            strSentence = "";

        }

        public void getNames(String strFile)
        {
            try
            {
                String strWord=null;
                Scanner s = new Scanner(new File(strFile)).useDelimiter("\n");;
                ArrayList<String> arrList = new ArrayList<String>();
                while (s.hasNext())
                {
                    strWord = s.next();
                    if(isEndWord(strWord + " "))
                    {
                        arrList.add(strWord + "|");
                    }
                    else
                    {
                        arrList.add(strWord);
                    }
                }
                s.close();    
                alNames = arrList;

            }
            catch(Exception e)
            {
                System.out.println(e.toString());
            } 
        } 

        public void getByWord(String strFile)
        {
            try
            {
                String strWord=null;
                Scanner s = new Scanner(new File(strFile));
                ArrayList<String> arrList = new ArrayList<String>();
                while (s.hasNext())
                {
                    strWord = s.next();
                    if(isEndWord(strWord + " "))
                    {
                        arrList.add(strWord + "|");
                    }
                    else
                    {
                        arrList.add(strWord);
                    }
                }
                s.close();    
                alWords = arrList;

            }
            catch(Exception e)
            {
                System.out.println(e.toString());
            } 
        }  

        private boolean isEndWord(String strWord)
        {
           int loopCnt = alBoundaries.size();
           int intIndex = -1;
           String strCompare = null;
           String strWord2 = strWord.replace(" ","");

           for(int i=0;i<loopCnt;i++)
           {
               strCompare = alBoundaries.get(i);
               intIndex = strWord.indexOf(strCompare);
               if(intIndex > -1 && Right(strWord,2).equals(strCompare))
               {
                   return true;
               }
           }

           for(int i=0;i<loopCnt;i++)
           {
               strCompare = alBoundaries.get(i);
               intIndex = strWord2.indexOf(strCompare);
               if(intIndex > -1 && Right(strWord2,2).equals(strCompare))
               {
                   return true;
               }
           }       
           return false; 
        }

        private void tagNames()
        {
           int loopCnt = alSentences.size();
           int intNameCnt = alNames.size();

           for(int i=0;i<loopCnt;i++)
           {           
               for(int j=0;j<intNameCnt;j++)
               {
                    if(alSentences.get(i).indexOf(alNames.get(j)) > -1 && alNames.get(j).length() > 0)
                    {
                        //alSentences.set(i,alSentences.get(i).replace(alNames.get(j), "<properNoun>" + alNames.get(j) + "</properNoun>"));
                        alSentences.set(i,alSentences.get(i).replaceAll("\\b" + alNames.get(j) + "\\b", "<properNoun>" + alNames.get(j) + "</properNoun>"));
                    }
               }
                //System.out.println(alSentences.get(i));
           }

        }

        public String Left(String strInput, int intLength)
        {
            try
            {
                strInput = strInput.substring(0,intLength);
                return(strInput);
            }
            catch(Exception e)
            {
                System.err.println("Left(): " + e.toString());
                return(strInput);
            }
        } 

        public String Right(String strInput, int intLength)
        {
            int intStart = 0;
            int intEnd = 0;

            try
            {
                intEnd  = strInput.length();
                intStart = intEnd - intLength;
                strInput = strInput.substring(intStart,intEnd);
                return(strInput);
            }
            catch (Exception e)
            {
                System.err.println("Right(): " + e.toString());
                return(strInput);
            }
        }

        private void writeToFile(String strContent, String strFile)
        {
            try 
            {
                File file = new File(strFile);

                if (!file.exists()) 
                {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(strContent);
                bw.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }             
 
    } //end workerThread class

    

        
} //end test3 class

class test2
{
    private ArrayList<String> alBoundaries = new ArrayList<String>();
    private ArrayList<String> alWords = new ArrayList<String>();
    private ArrayList<String> alSentences = new ArrayList<String>();
    private ArrayList<String> alNames = new ArrayList<String>();
    
    test2()
    {
        alBoundaries.add("\n");
        alBoundaries.add(".\"");
        alBoundaries.add("? ");
        alBoundaries.add(". ");
        alBoundaries.add(". ");
        alBoundaries.add("! ");
        
        getByWord("C:\\test\\input\\nlp_data.txt");
        getNames("C:\\test\\input\\NER.txt");
        assembleSentences();
        tagNames();
        outputToXML();

    }
    
    public void outputToXML()
    {
        try
        {
            int loopCnt=0;
            StringBuilder strXML = new StringBuilder("<?xml version=\"1.0\"?>");
            strXML.append("<object javaVersion=\"1.8.0_92\" class=\"test.test2\">");
            
            strXML.append("\n");
            strXML.append("<properties>");
            
                strXML.append("\n");
                strXML.append("<property name=\"alBoundaries\" type=\"ArrayList[String]\" description=\"Contains known sentence boundaries from text input\">");
                loopCnt=alBoundaries.size();
                for(int i=0;i<loopCnt;i++)
                {
                    strXML.append("\n");
                    if(alBoundaries.get(i).equals("\n"))
                        strXML.append("<propertyValue index=\"" + i + "\">\\n</propertyValue>");
                    else
                        strXML.append("<propertyValue index=\"" + i + "\">" + alBoundaries.get(i) + "</propertyValue>");
                }
                strXML.append("\n");
                strXML.append("</property>");

                strXML.append("\n\n");
                strXML.append("<property name=\"alSentences\" type=\"ArrayList[String]\" description=\"Contains sentences from text input\">");
                loopCnt=alSentences.size();
                for(int i=0;i<loopCnt;i++)
                {
                    strXML.append("\n");
                    strXML.append("<propertyValue index=\"" + i + "\">" + alSentences.get(i) + "</propertyValue>");
                }
                strXML.append("\n");
                strXML.append("</property>");

                strXML.append("\n\n");
                strXML.append("<property name=\"alWords\" type=\"ArrayList[String]\" description=\"Contains words from text input\">");            
                loopCnt=alWords.size();
                for(int i=0;i<loopCnt;i++)
                {
                    strXML.append("\n");
                    strXML.append("<propertyValue index=\"" + i + "\">" + alWords.get(i) + "</propertyValue>");
                }
                strXML.append("</property>");

            strXML.append("\n");
            strXML.append("</properties>");                
                
            strXML.append("\n\n");
            strXML.append("<methods>");  
                strXML.append("\n");
                strXML.append("<method name=\"Left\" parameters=\"[String],[int]\" description=\"Returns characters from left side of a string\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"Right\" parameters=\"[String],[int]\" description=\"Returns characters from right side of a string\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"assembleSentences()\" parameters=\"\" description=\"Assembles text input words into sentences and stores in ArrayList[String]\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"getByWord\" parameters=\"\" description=\"Gets words from text input and stores in ArrayList[String]\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"isEndWord\" parameters=\"[String]\" description=\"Marks words that fall at the end of a sentence\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"outputToXML\" parameters=\"\" description=\"Outputs object schema in readable XML\"></method>");            
            
            strXML.append("\n");
            strXML.append("</methods>");             
            strXML.append("</object>");             
            
            
            System.out.println(strXML.toString());
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }         
    }
    
    private void assembleSentences()
    {
        int loopCnt = alWords.size();
        String strSentence="";
              
        for(int i=0;i<loopCnt;i++)
        {
            strSentence += alWords.get(i) + " ";
            if(Right(alWords.get(i),1).equals("|"))
            {
                if(i+1 < loopCnt)
                {
                     if(Character.isUpperCase(alWords.get(i+1).charAt(0)))
                     {
                        alSentences.add(strSentence.replace("|",""));
                        strSentence = "";                         
                     }
                }
                else if(i+1 == loopCnt)
                {
                    alSentences.add(strSentence.replace("|",""));
                    strSentence = "";                     
                }
            }
        }
        strSentence = "";
        
    }

    public void getNames(String strFile)
    {
        try
        {
            String strWord=null;
            Scanner s = new Scanner(new File(strFile)).useDelimiter("\n");;
            ArrayList<String> arrList = new ArrayList<String>();
            while (s.hasNext())
            {
                strWord = s.next();
                if(isEndWord(strWord + " "))
                {
                    arrList.add(strWord + "|");
                }
                else
                {
                    arrList.add(strWord);
                }
            }
            s.close();    
            alNames = arrList;
            
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        } 
    } 
    
    public void getByWord(String strFile)
    {
        try
        {
            String strWord=null;
            Scanner s = new Scanner(new File(strFile));
            ArrayList<String> arrList = new ArrayList<String>();
            while (s.hasNext())
            {
                strWord = s.next();
                if(isEndWord(strWord + " "))
                {
                    arrList.add(strWord + "|");
                }
                else
                {
                    arrList.add(strWord);
                }
            }
            s.close();    
            alWords = arrList;
            
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        } 
    }  
    
    private boolean isEndWord(String strWord)
    {
       int loopCnt = alBoundaries.size();
       int intIndex = -1;
       String strCompare = null;
       String strWord2 = strWord.replace(" ","");
              
       for(int i=0;i<loopCnt;i++)
       {
           strCompare = alBoundaries.get(i);
           intIndex = strWord.indexOf(strCompare);
           if(intIndex > -1 && Right(strWord,2).equals(strCompare))
           {
               return true;
           }
       }

       for(int i=0;i<loopCnt;i++)
       {
           strCompare = alBoundaries.get(i);
           intIndex = strWord2.indexOf(strCompare);
           if(intIndex > -1 && Right(strWord2,2).equals(strCompare))
           {
               return true;
           }
       }       
       return false; 
    }

    private void tagNames()
    {
       int loopCnt = alSentences.size();
       int intNameCnt = alNames.size();
       
       for(int i=0;i<loopCnt;i++)
       {           
           for(int j=0;j<intNameCnt;j++)
           {
                if(alSentences.get(i).indexOf(alNames.get(j)) > -1 && alNames.get(j).length() > 0)
                {
                    //alSentences.set(i,alSentences.get(i).replace(alNames.get(j), "<properNoun>" + alNames.get(j) + "</properNoun>"));
                    alSentences.set(i,alSentences.get(i).replaceAll("\\b" + alNames.get(j) + "\\b", "<properNoun>" + alNames.get(j) + "</properNoun>"));
                }
           }
            //System.out.println(alSentences.get(i));
       }

    }
    
    public String Left(String strInput, int intLength)
    {
        try
        {
            strInput = strInput.substring(0,intLength);
            return(strInput);
        }
        catch(Exception e)
        {
            System.err.println("Left(): " + e.toString());
            return(strInput);
        }
    } 

    public String Right(String strInput, int intLength)
    {
        int intStart = 0;
        int intEnd = 0;

        try
        {
            intEnd  = strInput.length();
            intStart = intEnd - intLength;
            strInput = strInput.substring(intStart,intEnd);
            return(strInput);
        }
        catch (Exception e)
        {
            System.err.println("Right(): " + e.toString());
            return(strInput);
        }
    }
} //end test2 class


class test1
{
    private ArrayList<String> alBoundaries = new ArrayList<String>();
    private ArrayList<String> alWords = new ArrayList<String>();
    private ArrayList<String> alSentences = new ArrayList<String>();
    
    test1()
    {
        alBoundaries.add("\n");
        alBoundaries.add(".\"");
        alBoundaries.add("? ");
        alBoundaries.add(". ");
        alBoundaries.add(". ");
        alBoundaries.add("! ");
        
        getByWord("C:\\test\\input\\nlp_data.txt");
        assembleSentences();
        outputToXML();

    }
    
    public void outputToXML()
    {
        try
        {
            int loopCnt=0;
            StringBuilder strXML = new StringBuilder("<?xml version=\"1.0\"?>");
            strXML.append("<object javaVersion=\"1.8.0_92\" class=\"test.test1\">");
            
            strXML.append("\n");
            strXML.append("<properties>");
            
                strXML.append("\n");
                strXML.append("<property name=\"alBoundaries\" type=\"ArrayList[String]\" description=\"Contains known sentence boundaries from text input\">");
                loopCnt=alBoundaries.size();
                for(int i=0;i<loopCnt;i++)
                {
                    strXML.append("\n");
                    if(alBoundaries.get(i).equals("\n"))
                        strXML.append("<propertyValue index=\"" + i + "\">\\n</propertyValue>");
                    else
                        strXML.append("<propertyValue index=\"" + i + "\">" + alBoundaries.get(i) + "</propertyValue>");
                }
                strXML.append("\n");
                strXML.append("</property>");

                strXML.append("\n\n");
                strXML.append("<property name=\"alSentences\" type=\"ArrayList[String]\" description=\"Contains sentences from text input\">");
                loopCnt=alSentences.size();
                for(int i=0;i<loopCnt;i++)
                {
                    strXML.append("\n");
                    strXML.append("<propertyValue index=\"" + i + "\">" + alSentences.get(i) + "</propertyValue>");
                }
                strXML.append("\n");
                strXML.append("</property>");

                strXML.append("\n\n");
                strXML.append("<property name=\"alWords\" type=\"ArrayList[String]\" description=\"Contains words from text input\">");            
                loopCnt=alWords.size();
                for(int i=0;i<loopCnt;i++)
                {
                    strXML.append("\n");
                    strXML.append("<propertyValue index=\"" + i + "\">" + alWords.get(i) + "</propertyValue>");
                }
                strXML.append("</property>");

            strXML.append("\n");
            strXML.append("</properties>");                
                
            strXML.append("\n\n");
            strXML.append("<methods>");  
                strXML.append("\n");
                strXML.append("<method name=\"Left\" parameters=\"[String],[int]\" description=\"Returns characters from left side of a string\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"Right\" parameters=\"[String],[int]\" description=\"Returns characters from right side of a string\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"assembleSentences()\" parameters=\"\" description=\"Assembles text input words into sentences and stores in ArrayList[String]\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"getByWord\" parameters=\"\" description=\"Gets words from text input and stores in ArrayList[String]\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"isEndWord\" parameters=\"[String]\" description=\"Marks words that fall at the end of a sentence\"></method>");

                strXML.append("\n");
                strXML.append("<method name=\"outputToXML\" parameters=\"\" description=\"Outputs object schema in readable XML\"></method>");            
            
            strXML.append("\n");
            strXML.append("</methods></object>");             
            
            
            System.out.println(strXML.toString());
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }         
    }
    
    private void assembleSentences()
    {
        int loopCnt = alWords.size();
        String strSentence="";
              
        for(int i=0;i<loopCnt;i++)
        {
            strSentence += alWords.get(i) + " ";
            if(Right(alWords.get(i),1).equals("|"))
            {
                if(i+1 < loopCnt)
                {
                     if(Character.isUpperCase(alWords.get(i+1).charAt(0)))
                     {
                        alSentences.add(strSentence.replace("|",""));
                        strSentence = "";                         
                     }
                }
                else if(i+1 == loopCnt)
                {
                    alSentences.add(strSentence.replace("|",""));
                    strSentence = "";                     
                }
            }
        }
        strSentence = "";
        
    }
    
    public void getByWord(String strFile)
    {
        try
        {
            String strWord=null;
            Scanner s = new Scanner(new File(strFile));
            ArrayList<String> arrList = new ArrayList<String>();
            while (s.hasNext())
            {
                strWord = s.next();
                if(isEndWord(strWord + " "))
                {
                    arrList.add(strWord + "|");
                }
                else
                {
                    arrList.add(strWord);
                }
            }
            s.close();    
            alWords = arrList;
            
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        } 
    }  
    
    private boolean isEndWord(String strWord)
    {
       int loopCnt = alBoundaries.size();
       int intIndex = -1;
       String strCompare = null;
       String strWord2 = strWord.replace(" ","");
       
       for(int i=0;i<loopCnt;i++)
       {
           strCompare = alBoundaries.get(i);
           intIndex = strWord.indexOf(strCompare);
           if(intIndex > -1 && Right(strWord,2).equals(strCompare))
           {
               return true;
           }
       }

       for(int i=0;i<loopCnt;i++)
       {
           strCompare = alBoundaries.get(i);
           intIndex = strWord2.indexOf(strCompare);
           if(intIndex > -1 && Right(strWord2,2).equals(strCompare))
           {
               return true;
           }
       }       
       
       
       return false; 
    }
    
    public String Left(String strInput, int intLength)
    {
        try
        {
            strInput = strInput.substring(0,intLength);
            return(strInput);
        }
        catch(Exception e)
        {
            System.err.println("Left(): " + e.toString());
            return(strInput);
        }
    } 

    public String Right(String strInput, int intLength)
    {
        int intStart = 0;
        int intEnd = 0;

        try
        {
            intEnd  = strInput.length();
            intStart = intEnd - intLength;
            strInput = strInput.substring(intStart,intEnd);
            return(strInput);
        }
        catch (Exception e)
        {
            System.err.println("Right(): " + e.toString());
            return(strInput);
        }
    }
} //end test1 class

