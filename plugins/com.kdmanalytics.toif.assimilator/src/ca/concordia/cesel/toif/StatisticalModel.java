/*
The MIT License (MIT)

Copyright (c) 2015 Louis-Philippe Querel, Concordia University CESEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package ca.concordia.cesel.toif;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;

public class StatisticalModel {

    private Float filterThreshold = 0.5f;

    private ProcessedFiles processedFiles;

    private Integer fileChurnThreshold;
    private ConcurrentSkipListMap<String, Integer> map;

    public StatisticalModel(){

        File processed = new File("processed.json");

        if (processed.exists()){
            try {
                Scanner fileReader = new Scanner(processed);
                String file = fileReader.nextLine();

                Gson parser = new Gson();
                processedFiles = parser.fromJson(file, ProcessedFiles.class);
                System.out.println(processedFiles.version);
                System.out.println(processedFiles.file_list.size());

//                TODO we'd want to use the median instead of the mean
//                Go through the files to determine cutoff
                Integer sumofFileChurns = 0;
                map = new ConcurrentSkipListMap();

                for (ProcessedFile processedFile: processedFiles.file_list){
                    String[] filePath = processedFile.file.split("/");
//                    Given that we need to match a.class to a.java we need to ignore file extensions for now
                    System.out.println(processedFile.file + " " + filePath.length + " " + (filePath.length - 1));
                    String fileName = filePath[filePath.length - 1];


                    String[] fileNameWithoutExtension = fileName.split("\\.|$");
                    System.out.println(fileName + " " + fileNameWithoutExtension.length);
                    map.put(fileNameWithoutExtension[0], processedFile.sum);


                    sumofFileChurns += processedFile.sum;
                }

                fileChurnThreshold = sumofFileChurns / processedFiles.file_list.size();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean doesModelExist() {

        return processedFiles != null;

    }

    public boolean filterFile(String filename){

        if (processedFiles != null){

            String trimmedFilename = filename.split("\\$")[0];

            if (!map.containsKey(trimmedFilename)) {
                System.err.println("File " + trimmedFilename + " is not in list");

            } else {
                if (map.get(trimmedFilename) < fileChurnThreshold){
                    return true;
                }
            }

        } else {
            System.out.println("Not active");
        }
        return false;

    }
}
