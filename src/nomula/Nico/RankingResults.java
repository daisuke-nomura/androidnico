package nomula.Nico;

import java.util.ArrayList;

public class RankingResults {
	private String Title, Description, Thumbnail, ID, Length, MovieType, UploadTime;
	private int CommentCount, ViewCount, MylistCount;
	private ArrayList<String> TagList;

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getThumbnail() {
		return Thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		Thumbnail = thumbnail;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getLength() {
		return Length;
	}

	public void setLength(String length) {
		Length = length;
	}

	public String getMovieType() {
		return MovieType;
	}

	public void setMovieType(String movieType) {
		MovieType = movieType;
	}

	public int getCommentCount(){
		return CommentCount;
	}

	public void setCommentCount(String commentCount){
		CommentCount = Integer.parseInt(commentCount);
	}

	public int getViewCount(){
		return ViewCount;
	}

	public void setViewCount(String viewCount){
		ViewCount = Integer.parseInt(viewCount);
	}

	public int getMylistCount(){
		return MylistCount;
	}

	public void setMylistCount(String mylistCount){
		MylistCount = Integer.parseInt(mylistCount);
	}

	public String getUploadTime() {
		return UploadTime;
	}

	public void setUploadTime(String uploadTime) {
//		java.util.Date date = null;
//		java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss+09:00");
//		try {
//			date = df.parse(uploadTime);
//		} catch (ParseException e) {
//		}
//		String[] str = uploadTime.split("+");
		UploadTime =  uploadTime;
	}

	public ArrayList<String> getTagList(){
		return TagList;
	}

	public String getTagListAll(){
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < TagList.size(); i++){
			sb.append(TagList.get(i));
			sb.append(" ");
		}

		return sb.toString();
	}

	public void setTagList(ArrayList<String> list){
		TagList = list;
	}
}
