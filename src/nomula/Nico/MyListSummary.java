package nomula.Nico;

public class MyListSummary {
	private int MyListID;
	private String Name;

	public int getMyListID() {
		return MyListID;
	}

	public void setMyListID(String myListID) {
		MyListID = Integer.parseInt(myListID);
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
}
