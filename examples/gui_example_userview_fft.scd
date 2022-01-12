//fft gui example with userview / pen graphics
(
var width= 640, height= 480;
var numChan= 4;
var chanHeight= height/numChan;
var win, usr, f;
win= Window("fft gui example userview", Rect(100, 100, width, height)).front;
usr= UserView(win, Rect(0, 0, width, height));
usr.animate= true;
usr.frameRate= 60;
usr.background= Color.black;
usr.clearOnRefresh= false;
usr.drawFunc= {
	var x= usr.frame%width;
	var h= height/numChan/f.fft[0].size;
	//f.fft.postln;  //debug
	f.fft.do{|arr, cy|
		arr.do{|val, y|
			Pen.fillColor= Color.grey(val*0.1);
			Pen.fillRect(Rect(x, (cy+1*chanHeight)-y, 1, h));
		};
	};
};
~ibva= IBVA("/dev/tty.BlueVAS_H-Data");  //replace IBVA with IBVAfake for test data
f= IBVAfft(~ibva);
f.start;
CmdPeriod.doOnce({win.close});
)


~ibva.setFilterCutoff(0.5);
~ibva.setFilterCutoff(0.25);
~ibva.setFilterCutoff(0.75);
~ibva.sr= 200;
