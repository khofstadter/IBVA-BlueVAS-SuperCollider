//gui example with scrolling multisliders
(
var history= 150;  //how many sliders to fit in each multislider
var numChan= 4;
var lastTime= 0;
var win, msliders, updateFunc;
win= Window("gui example scrolling", Rect(100, 100, 640, 480)).front;
msliders= {
	MultiSliderView()
	.canFocus_(false)
	.isFilled_(true)
	.colors_(Color.black, Color.grey)
	.value_(0.dup(history))
	.elasticMode_(1);
}.dup(numChan);
win.layout= VLayout(*msliders);
updateFunc= {|...args|
	if(Main.elapsedTime-lastTime>0.016, {  //downsample to 60fps
		lastTime= Main.elapsedTime;
		{
			args.do{|x, i|
				var vals= msliders[i].value;
				msliders[i].value= (x/1023).asArray++vals.copyRange(0, history-2);
			};
		}.defer;
	});
};
~ibva= IBVA("/dev/tty.BlueVAS_H-Data", updateFunc, 60, 0.3);  //replace IBVA with IBVAfake for test data
CmdPeriod.doOnce({win.close});
)



~ibva.setSamplingRate(200);
~ibva.setFilterCutoff(0.5);
~ibva.setFilterCutoff(0.25);
~ibva.setFilterCutoff(0.75);
