/*
 * @(#)FileUtils.java
 */
package kr.co.photointerior.kosw.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * This class contains utility methods for handles files.
 * Created by kugie.
 * 2018. 04. 30.
 */
public class FileUtils {
	/**
	 * Line Separator
	 */
	public static final String LINE_SEP = System.getProperty("line.separator");
    /**
     * Returns {@link File}.
     * <pre>
     * 		usage : File f = getFile( "./resources/somfile.xml" );
     * </pre>
     * @param name
     * @return File 객체
     */
    public static File getFile(String name) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return new File(loader.getResource(name).getFile());
    }
    /**
     * Returns {@link InputStream} of {@link File}.
     * <pre>
     * 		usage : InputStream in = getFileAsStream( "./resources/somfile.xml" );
     * </pre>
     * @param name
     * @return InputStream
     */
    public static InputStream getFileAsStream( String name ){
    	ClassLoader loader = Thread.currentThread().getContextClassLoader();
    	return loader.getResourceAsStream(name);
    }
	/**
	 * Takes the filename on absolute path, reads the contents, and returns it as a {@link String}.
	 * @param fileName absolute file path
	 * @return file contents
	 */
	public static String loadFromFile(String fileName) {
		return loadFromFile(new File(fileName));
	}

	/**
	 * Takes the filename on absolute path, reads the contents, and returns it as a {@link String}.
	 * @param path file path
	 * @param fileName file name
	 * @return file contents
	 */
	public static String loadFromFile(String path, String fileName) {
		return loadFromFile(new File(path, fileName));
	}

	/**
	 * Takes the filename on absolute path, reads the contents, and returns it as a {@link String}.
	 * @param file File instance
	 * @return file contents
	 */
	public static String loadFromFile(File file) {
		StringBuffer sb = new StringBuffer(4096);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String read = "";
			while ((read = br.readLine()) != null) {
				sb.append(read + LINE_SEP);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return sb.toString();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return sb.toString();
			}
		}
		return sb.toString();
	}

	/**
	 * Takes the filename on absolute path, reads the contents, and returns it as a {@link String}.
	 * @param path file path
	 * @param fileName file name
	 * @param charSet charset
	 * @return
	 */
	public static String loadFromTextFile(String path, String fileName, String charSet){
		return loadFromTextFile(new File(path, fileName), charSet);
	}

	/**
	 * Takes the filename on absolute path, reads the contents, and returns it as a {@link String}.
	 * @param file File instance
	 * @return file contents
	 */
	public static String loadFromTextFile(File file, String charSet) {
		StringBuffer sb = new StringBuffer(4096);
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), charSet));
			String read = "";
			while ((read = br.readLine()) != null) {
				sb.append(read + LINE_SEP);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return sb.toString();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return sb.toString();
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the contents from {@link InputStream}.
	 * @param inputStream inputstream
	 * @param charSet charset
	 * @return
	 */
	public static String loadFromTextFile(InputStream inputStream, String charSet){
		StringBuffer result = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream, charSet));
			String read = "";
			while ((read = br.readLine()) != null) {
				result.append(read + LINE_SEP);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}finally{
			if( br != null ){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result.toString();
	}

	/**
	 * Writes the contents to {@link File}.
	 * @param fileName absolute file path
	 * @param content contents
	 */
	public static void saveToFile(
		String fileName,
		String content) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(fileName)));
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Save the contents to {@link File}.
	 * @param filePath filepath
	 * @param fileName filename
	 * @param content contents
	 */
	public static boolean saveToFile(
		String filePath, String fileName,
		String content) {
		boolean result = false;
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(filePath, fileName)));
			bw.write(content);
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * Delete the file.
	 * @param fileName source filepath
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		File f = new File(fileName);
		if (!f.isDirectory() && f.exists()) {
			return f.delete();
		}else
			return false;
	}

	/**
	 * Returns last modification time of file as long.
	 * @param fileName absolute path
	 * @return last modification time.
	 */
	public static long getTime(String fileName) {
		File f = new File(fileName);
		if (!f.isDirectory() && f.exists()) {
			return f.lastModified();
		}
		return 0L;
	}

	/**
	 * Tests a file is newer.
	 * @param absPath absolute filepath
	 * @param lastModifiedTime file creation time.
	 * @return if 'true' the file is newer.
	 */
	public static boolean isNew(String absPath, long lastModifiedTime) {
		return isNew( new File( absPath ), lastModifiedTime );
	}

	/**
	 * Tests a file is newer.
	 * @param file source file
	 * @param lastModifiedTime file creation time.
	 * @return if 'true' the file is newer.
	 */
	public static boolean isNew(File file, long lastModifiedTime) {
		if (file == null)
			return false;
		if (!file.exists() || !file.isFile())
			return false;
		return file.lastModified() > lastModifiedTime;
	}

	/**
	 * Tests a file is newer.
	 * @param filePath filepath
	 * @param fileName filename
	 * @param lastModifiedTime file creation time.
	 * @return if 'true' the file is newer.
	 */
	public static boolean isNew(String filePath, String fileName, long lastModifiedTime) {
		return isNew( new File( filePath, fileName ), lastModifiedTime );
	}

	/**
	* Delete the entire file in the absolute path on disk.
	* The target directory will be deleted.
	* @param path absolute path
	*/
	public static void deleteAll( String path ){
		File f = new File( path );
		File[] fileList = f.listFiles();
		if( fileList != null ){
			for( int i = 0, k = fileList.length; i < k; i++ ){
				if( fileList[i].exists() ){
					if( fileList[i].isDirectory()){
						deleteAll(  fileList[i].getAbsolutePath() );
					}
					fileList[i].delete();
				}
			}
			f.delete();
		}
	}
	/**
	 * Delete the entire file in the absolute path on disk.
	 * If recursive is true, all subdirectories are deleted.
	 * If recursive is false, delete only the files in th directory.
	 * @param absPath delete target path
	 * @param recursive true-recursive
	 */
	public static void deleteAll( String absPath, boolean recursive ){
		if( recursive ){
			deleteAll( absPath );
		}else{
			File f = new File( absPath );
			File[] fileList = null;
			fileList = f.listFiles();
			if( fileList != null ){
				for( int i = 0, k = fileList.length; i < k; i++ ){
					if( fileList[i].exists() ){
						if( fileList[i].isFile()){
							fileList[i].delete();
						}
					}
				}
				f = new File( absPath );
				fileList = f.listFiles();
				if( fileList != null && fileList.length == 0 ){
					f.delete();
				}
			}	
		}
	}
	/**
	* Rename a filename.
	* @param file source {@link File}
	* @param path target filepath
	* @param newName target filename
	*/
	public static void rename(
		File file,
		String path,
		String newName) {
		if (file.exists())
			file.renameTo(new File(path, newName));
	}
	/**
	* Rename a filename.
	* @param sourceFile
	* @param targetFileName
	*/
	public static void rename( File sourceFile, String targetFileName ){
		rename( sourceFile, new File( targetFileName ) );
	}

	/**
	* Rename a filename.
	* @param sourceFile
	* @param targetFile
	*/
	public static void rename( File sourceFile, File targetFile ){
		if( sourceFile.exists() )
			sourceFile.renameTo( targetFile );
	}
	/**
	 * Renames a file.
	 * @param sourceFileName
	 * @param targetFileName
	 */
	public static void rename( String sourceFileName, String targetFileName ){
		rename( new File( sourceFileName ), targetFileName );
	}

	/**
	* Copy a file.
	*
	* @param in InputStream of source file
	* @param out OutputStream of target file
	* @throws IOException
	*/
	public static void copy( 
			InputStream in, OutputStream out )throws IOException{
		byte[] buffer = new byte[1024];
		int bytesRead = -1;
		while( ( bytesRead = in.read(buffer) ) != -1 ){
			out.write(buffer, 0, bytesRead);
		}
	}
	
	/**
	 * Copy file to another file.
	 * @param sourceFile source file with absolute path
	 * @param targetFile target file with absolute path
	 */
	public static void copy( String sourceFile, String targetFile ){
		copy(new File(sourceFile), new File(targetFile));
	}

	/**
	 * Copy file to another file.
	 * @param sourceFile source file with absolute path
	 * @param targetFile target file with absolute path
	 * @param createDirectories if 'true' and the directory not exists, create directory.
	 */
	public static void copy( 
			String sourceFile, String targetFile, boolean createDirectories ){
		FileInputStream fileInput = null;
		FileOutputStream fileOutput = null;
		try{
			if( createDirectories ){
				String path = targetFile.substring(0, targetFile.lastIndexOf( File.separator ) );
				File f = new File( path );
				if( !f.exists() ){
					createDirectories( path );
				}
			}
			File f = new File( sourceFile ); 
			if( f.exists() ){
				copy(f, new File(targetFile));
			}
		}catch(FileNotFoundException ffe){
			ffe.printStackTrace();
		}catch ( IOException ioE ){
			ioE.printStackTrace();
		}finally{
			if( fileInput != null ){
				try{
					fileInput.close();
				}catch ( IOException e1 ){}
			}
			if( fileOutput != null ){
				try{
					fileOutput.close();
				}catch ( IOException e1 ){}
			}
		}
	}
	/**
	 * Creates the direcotries.
	 * @param path
	 * @throws IOException
	 */
	public static void createDirectories( String path ) throws IOException{
		File targetDirectory = new File( path );
		if(!targetDirectory.exists()){
			targetDirectory.mkdirs();
		}
	}

	/**
	 * Copy file to another file.
	 * 
	 * @param sourceFile source file
	 * @param targetFile target file
	 */
	public static void copy( File sourceFile, File targetFile ){
		FileInputStream fileInput = null;
		FileOutputStream fileOutput = null;
		try{
			if( sourceFile != null && sourceFile.exists() ){
				fileInput = new FileInputStream( sourceFile );
				fileOutput = new FileOutputStream( targetFile ); 
				copy(fileInput, fileOutput);
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			if( fileInput != null ){
				try{
					fileInput.close();
				}catch ( IOException e1 ){}
			}
			if( fileOutput != null ){
				try{
					fileOutput.close();
				}catch ( IOException e1 ){}
			}
		}
	}

	/**
	 * Copy file to another directory.
	 * @param sourceDirectory source filepath
	 * @param sourceFile source filename
	 * @param targetDirectory target filepath
	 * @param targetFile target filename
	 */
	public static void copy( 
			String sourceDirectory, String sourceFile,
			String targetDirectory, String targetFile ){
		copy(sourceDirectory + File.separator + sourceFile,
				targetDirectory + File.separator + targetFile);
	}

	/**
	* Tests whether a file exists.
	* @param fileName absolute path of file.
	* @return boolean
	*/
	public static boolean isExistFile( String fileName ){
		return new File( fileName ).exists();
	}

	/**
	* Tests whether a file exists.
	* @param absolutePath filepath
	* @param fileName filename
	* @return boolean
	*/
	public static boolean isExistFile( String absolutePath, String fileName ){
		return new File( absolutePath, fileName ).exists();
	}

	/**
	 * Removes directories and files.
	 * @param directory
	 * @param fileName
	 * @param removeEmptyDirectory if 'true' remove empty directory.
	 * @throws TaskFailException
	 */
	public static void deleteSpecifiedFile(
			String directory, String fileName, boolean removeEmptyDirectory)
			throws TaskFailException{
		if(directory == null || fileName == null)
			throw new TaskFailException("Directory or file name can't be null.");
		
		File targetDirectory = new File(directory);
		
		if(targetDirectory.isDirectory() && targetDirectory.exists()){
			File[] fileList = targetDirectory.listFiles();
			if(fileList != null && fileList.length > 0){
				for( int i = 0, k = fileList.length; i < k; i++){
					if(fileList[i].isDirectory()){
						deleteSpecifiedFile(
								fileList[i].getAbsolutePath(), fileName, removeEmptyDirectory);
					}else{
						if(fileList[i].getName().equals(fileName)){
							fileList[i].delete();
						}
					}
				}
				if(removeEmptyDirectory){
					fileList = targetDirectory.listFiles();
					if(fileList.length == 0)
						targetDirectory.delete();
				}
			}else{
				if(removeEmptyDirectory){
					targetDirectory.delete();
				}
			}
		}else{
			throw new TaskFailException("Invalid directory-" + directory);
		}
	}
	/**
	 * Returns the character after "/" or "\" in the file path.
	 * @param source
	 * @return
	 */
	public static String takeLast(String source){
		return source.replaceFirst("^.*(/|\\\\)", "");
	}
}