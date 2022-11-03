//fft gui example with multisliders
(
var numChan= 4;
var win, msliders;
win= Window("fft gui example scrolling", Rect(100, 100, 640, 480)).front;
msliders= {
	MultiSliderView()
	.canFocus_(false)
	.isFilled_(true)
	.colors_(Color.black, Color.grey)
	.value_([0])
	.elasticMode_(1);
}.dup(numChan);
win.layout= VLayout(*msliders);
~ibva= IBVA("/dev/tty.BlueVAS_H-Data");  //replace IBVA with IBVAfake for test data
Routine({
	var f= IBVAfft(~ibva);
	f.start;
	inf.do{
		msliders.do{|m, i|
			m.value= f.fft[i].dbamp-1*0.1;
		};
		(1/60).wait;  //framerate
	};
}).play(AppClock);
CmdPeriod.doOnce({win.close});
)



~ibva.setSamplingRate(200);
~ibva.setFilterCutoff(0.5);
~ibva.setFilterCutoff(0.25);
~ibva.setFilterCutoff(0.75);
