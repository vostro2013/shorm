package com.xiaowen.shorm.compiler;

import com.xiaowen.shorm.compiler.utils.LicenseHeaders;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author: wenc.hao
 * @date: 2018/3/25
 * @since:
 */
public class ShormCompilerCLI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShormCompilerCLI.class);

    public static void main(String[] args) {
        if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
            printHelp();
            System.exit(0);
        }
        if (args.length < 2) {
            LOGGER.error("Must supply at least one source file and an output directory.");
            printHelp();
            System.exit(1);
        }
        // Setting the default license header to ASLv2
        LicenseHeaders licenseHeader = new LicenseHeaders("ASLv2");
        // Checking for user provided license
        for (int i = 0; i < args.length; i++) {
            if ("-license".equals(args[i])) {
                if (i == args.length - 1) {
                    LOGGER.error("Must supply a valid license id.");
                    printHelp();
                    System.exit(1);
                }
                if (licenseHeader.isValidLicense(args[i + 1])) {
                    licenseHeader.setLicenseName(args[i + 1]);
                    args = (String[]) ArrayUtils.removeElement(args, args[i + 1]);
                    args = (String[]) ArrayUtils.removeElement(args, args[i]);
                } else {
                    LOGGER.error("Must supply a valid license id.");
                    printHelp();
                    System.exit(1);
                }
            }
        }

        File outputDir = new File(args[args.length - 1]);
        if (!outputDir.isDirectory()) {
            LOGGER.error("Must supply a directory for output");
            printHelp();
            System.exit(1);
        }
        // Processing input directory or input files
        File inputDir = new File(args[0]);
        File[] inputFiles = null;
        if (inputDir.isDirectory()) {
            if (inputDir.length() > 0)
                inputFiles = inputDir.listFiles();
            else {
                LOGGER.error("Input directory must include at least one file.");
                printHelp();
                System.exit(1);
            }
        } else {
            inputFiles = new File[args.length - 1];
            for (int i = 0; i < inputFiles.length; i++) {
                File inputFile = new File(args[i]);
                if (!inputFile.isFile()) {
                    LOGGER.error("Input must be a file.");
                    printHelp();
                    System.exit(1);
                }
                inputFiles[i] = inputFile;
            }
        }
        try {
            ShormCompiler.compileSchema(inputFiles, outputDir, licenseHeader);
            LOGGER.info("Compiler executed successful.");
        } catch (IOException e) {
            LOGGER.error("Error while compiling schema files. Check that the schemas are properly formatted.");
            printHelp();
            throw new RuntimeException(e);
        }
    }

    private static void printHelp() {
        LOGGER.info("Usage: shorm-compiler ( -h | --help ) | (<input> [<input>...] <output> [-license <id>])");
        LOGGER.error("License header options include;\n" +
                "\t\t  ASLv2   (Apache Software License v2.0) \n" +
                "\t\t  AGPLv3  (GNU Affero General Public License) \n" +
                "\t\t  CDDLv1  (Common Development and Distribution License v1.0) \n" +
                "\t\t  FDLv13  (GNU Free Documentation License v1.3) \n" +
                "\t\t  GPLv1   (GNU General Public License v1.0) \n" +
                "\t\t  GPLv2   (GNU General Public License v2.0) \n" +
                "\t\t  GPLv3   (GNU General Public License v3.0) \n" +
                "\t\t  LGPLv21 (GNU Lesser General Public License v2.1) \n" +
                "\t\t  LGPLv3  (GNU Lesser General Public License v2.1)");
    }
}
