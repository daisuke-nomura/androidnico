package nomula.Nico;

import android.graphics.Color;

public class Comment {
	public String Text;
	public int Vpos;
	public double Speed;
	public double Top;
	public double Left;
	public double ToValue;
	public int FontSize;
	public int ShowedTime;
	public int Width;
	public String Mail;
	public int FontColor;
	public String Point;

	public String getMail() {
		return Mail;
	}
	public void setMail(String mail) {
		Mail = mail;
		getColor();
	}

	public void getColor(){
		FontSize = 40;
		FontColor = Color.WHITE;
		Point = "n";

		if (Mail.contains(" ")){
			String[] mail = Mail.split(" ");

			for (int i = 0; i < mail.length; i++){
				if (mail[i].equals(184))
					break;
				else if (mail[i].equals("black"))
					FontColor =  Color.BLACK;
				else if(mail[i].equals("cyan"))
					FontColor =  Color.CYAN;
				else if(mail[i].equals("blue"))
					FontColor =  Color.BLUE;
				else if(mail[i].equals("green"))
					FontColor =  Color.GREEN;
				else if(mail[i].equals("yellow"))
					FontColor =  Color.YELLOW;
				else if(mail[i].equals("red"))
					FontColor =  Color.RED;
				else if(mail[i].equals("orange"))
					FontColor =  Color.parseColor("#F39800");
				else if(mail[i].equals("pink"))
					FontColor =  Color.parseColor("#F8ABA6");
				else if(mail[i].equals("purple"))
					FontColor =  Color.parseColor("#A757A8");

				else if(mail[i].equals("big"))
					FontSize = 45;
				else if(mail[i].equals("small"))
					FontSize =  30;

				else if(mail[i].equals("shita"))
					Point = "s";
				else if(mail[i].equals("ue"))
					Point = "u";
				}
			}
		}
	}