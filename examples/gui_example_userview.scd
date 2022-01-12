//gui example with userview / pen graphics
(
var width= 640, height= 320;
var numChan= 4;
var chanHeight= height/numChan;
var win, usr;
win= Window("gui example userview", Rect(100, 100, width, height)).front;
usr= UserView(win, Rect(0, 0, width, height));
usr.animate= true;
usr.frameRate= 60;
usr.background= Color.black;
usr.clearOnRefresh= false;
usr.drawFunc= {
	var x= usr.frame%width;
	//~ibva.data.postln;  //debug
	Pen.fillColor= Color.grey(0, 0.5);  //alpha
	Pen.fillRect(Rect(x, 0, 1, height));
	Pen.fillColor= Color.white;
	~ibva.data.do{|val, y|
		Pen.fillRect(Rect(x, val.linlin(0, 1023, 0, chanHeight)+(y*chanHeight), 1, 1));
	};
};
~ibva= IBVA("/dev/tty.BlueVAS_H-Data", {}, 60, 0.3);  //replace IBVA with IBVAfake for test data
CmdPeriod.doOnce({win.close});
)


~ibva.setFilterCutoff(0.5);
~ibva.setFilterCutoff(0.25);
~ibva.setFilterCutoff(0.75);



//--record
a= IBVArecorder(~ibva);
a.prepare("~/Desktop/ibvatestrec_userview");
a.record;
a.stop;

//--playback
~ibva.close;
b= IBVAplayer(~ibva);
b.prepare("~/Desktop/ibvatestrec_userview");
b.play;
b.stop;

b.play(1.5, 2, {"done".postln});  //repeat twice at 150% rate and post when done
