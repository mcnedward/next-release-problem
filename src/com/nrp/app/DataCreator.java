package com.nrp.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.nrp.app.model.DataFile;

/**
 * @author Edward McNealy <edwardmcn64@gmail.com> - Oct 26, 2015
 *
 */
public class DataCreator {
	private String RESOURCE_DIRECTORY = "resources/";

	private List<DataFile> files;
	private DataFile file;

	@Inject
	public DataCreator() {
		files = new ArrayList<DataFile>();
	}

	/**
	 * Creates the DataFile with the data parsed for the file with the specified name.
	 * 
	 * @param fileName
	 *            The name of the file to parse. This file should be located in the resources package.
	 * @return The created DataFile.
	 */
	public DataFile createDataFile(String fileName) {
		if (readFile(fileName))
			file.parseFile();
		return file;
	}

	/**
	 * Find a DataFile based on the file name.
	 * 
	 * @param name
	 *            The name of file in the DataFile.
	 * @return The DataFile, or null if none found by that name.
	 */
	public DataFile getDataFileByName(String name) {
		for (DataFile file : files) {
			if (file.getFileName().equals(name))
				return file;
		}
		return null;
	}

	/**
	 * Reads the file from resource package and created a DataFile.
	 * 
	 * @param fileName
	 *            The name of the file to read.
	 * @return True if the file was created, false otherwise.
	 */
	private boolean readFile(String fileName) {
		String fileLocation = RESOURCE_DIRECTORY + fileName;
		System.out.println("Attempting to read file: " + fileLocation);
		InputStream stream = getClass().getResourceAsStream(fileLocation);
		if (stream == null) {
			System.out.println("Problem creating stream...");
			return false;
		}
		List<String> lines = readStream(stream);
		file = new DataFile(fileName, lines);
		System.out.println("Successfully created DataFile: " + file);
		return true;
	}

	/**
	 * Read an InputStream and convert the stream to lines of string.
	 * 
	 * @param stream
	 *            The InputStream to read.
	 * @return The list of strings in the stream.
	 */
	private List<String> readStream(InputStream stream) {
		List<String> lines = new ArrayList<String>();
		String line = "";

		try {
			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			streamReader.close();
			reader.close();
		} catch (IOException e) {
			System.out.println("Error in file: " + e.getMessage());
		}
		return lines;
	}

	/**
	 * @return the files
	 */
	public List<DataFile> getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(List<DataFile> files) {
		this.files = files;
	}

	/**
	 * @return the file
	 */
	public DataFile getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(DataFile file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "DataCreator";
	}
}
