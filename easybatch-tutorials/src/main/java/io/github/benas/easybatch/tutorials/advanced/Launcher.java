/*
 * The MIT License
 *
 *   Copyright (c) 2014, benas (md.benhassine@gmail.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */

package io.github.benas.easybatch.tutorials.advanced;

import io.github.benas.easybatch.core.api.EasyBatchReport;
import io.github.benas.easybatch.core.impl.EasyBatchEngine;
import io.github.benas.easybatch.core.impl.EasyBatchEngineBuilder;
import io.github.benas.easybatch.flatfile.FlatFileRecordReader;
import io.github.benas.easybatch.flatfile.dsv.DelimitedRecordMapper;
import io.github.benas.easybatch.jdbc.JdbcRecordMapper;
import io.github.benas.easybatch.jdbc.JdbcRecordReader;
import io.github.benas.easybatch.xml.XmlRecordMapper;
import io.github.benas.easybatch.xml.XmlRecordReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
* Main class to run the advanced ETL tutorial.
 *
* @author benas (md.benhassine@gmail.com)
*/
public class Launcher {

    public static void main(String[] args) throws Exception {

        //do not let hsqldb reconfigure java.util.logging used by easy batch
        System.setProperty("hsqldb.reconfig_logging", "false");

        /*
         * Start embedded database server
         */
        DatabaseUtil.startEmbeddedDatabase();
        DatabaseUtil.initializeSessionFactory();

        // Build an easy batch engine to read greetings from csv file
        EasyBatchEngine easyBatchCsvEngine = new EasyBatchEngineBuilder()
                .registerRecordReader(new FlatFileRecordReader(args[0]))
                .registerRecordMapper(new DelimitedRecordMapper<Greeting>(Greeting.class, new String[]{"sequence","name"}))
                .registerRecordProcessor(new GreetingDataLoader())
                .build();

        // Build an easy batch engine to read greetings from xml file
        EasyBatchEngine easyBatchXmlEngine = new EasyBatchEngineBuilder()
                .registerRecordReader(new XmlRecordReader("greeting", args[1]))
                .registerRecordMapper(new XmlRecordMapper<Greeting>(Greeting.class))
                .registerRecordProcessor(new GreetingDataLoader())
                .build();

        //create a 2 threads pool to call Easy Batch engines in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<EasyBatchReport> easyBatchReport1 = executorService.submit(easyBatchCsvEngine);
        Future<EasyBatchReport> easyBatchReport2 = executorService.submit(easyBatchXmlEngine);

        easyBatchReport1.get();
        easyBatchReport2.get();

        executorService.shutdown();

        // Build an easy batch engine to generate JSON products data from the database
        EasyBatchEngine easyBatchJsonEngine = new EasyBatchEngineBuilder()
                .registerRecordReader(new JdbcRecordReader(DatabaseUtil.getDatabaseConnection(), "select * from greeting"))
                .registerRecordMapper(new JdbcRecordMapper<Greeting>(Greeting.class))
                .registerRecordProcessor(new GreetingJsonGenerator())
                .build();

        easyBatchJsonEngine.call();

        //close database session factory
        DatabaseUtil.closeSessionFactory();

    }

}