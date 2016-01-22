package application;

public class DatabaseLine {
	int id;
	String fileName;
	String filePath;
	String shaSum;
	String IP;
	
	public DatabaseLine(int id, String fileName, String filePath, String shaSum, String IP) {
		this.id = id;
		this.fileName = fileName;
		this.filePath = filePath;
		this.shaSum = shaSum;
		this.IP = IP;
	}
	
	public DatabaseLine(String all) {
		String parts[] = all.split("\t");
		id = Integer.parseInt(parts[0]);
		fileName = parts[1];
		filePath = parts[2];
		shaSum = parts[3];
		IP = parts[4];
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getShaSum() {
		return shaSum;
	}
	public void setShaSum(String shaSum) {
		this.shaSum = shaSum;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String IP) {
		this.IP = IP;
	}

	public Boolean isFilename(String message) {
		if(fileName.contains(message))
			return true;
		else
			return false;
	}
	
	public Boolean isShaSum(String message) {
		if(shaSum.equals(message)) 
			return true;
		else
			return false;
	}
	
	public String toString() {
		return id + "\t" + fileName + "\t" + filePath + "\t" + shaSum + "\t" + IP;
	}
	
}
