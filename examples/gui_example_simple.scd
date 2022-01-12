//simple gui example just displaying data using sliders and numberboxes
(
var numChan= 4;
var lastValues= -1.dup(numChan);
var lastTime= 0;
var win, sliders, numbers, updateFunc;
win= Window("gui example simple", Rect(100, 100, 320, 240)).front;
sliders= {Slider().canFocus_(false)}.dup(numChan);
numbers= {NumberBox().canFocus_(false)}.dup(numChan);
win.layout= VLayout(*[
	HLayout(*sliders),
	HLayout(*numbers)
]);
updateFunc= {|...args|
	if(Main.elapsedTime-lastTime>0.016, {  //downsample to 60fps
		lastTime= Main.elapsedTime;
		args.do{|x, i|
			if(x!=lastValues[i], {  //filter out repeats
				lastValues[i]= x;
				{
					sliders[i].value= x/1023;
					numbers[i].value= x;
				}.defer;
			});
		};
	});
};
~ibva= IBVA("/dev/tty.BlueVAS_H-Data", updateFunc, 60, 0.3);  //replace IBVA with IBVAfake for test data
CmdPeriod.doOnce({win.close});
)


~ibva.setFilterCutoff(0.5);
~ibva.setFilterCutoff(0.25);
~ibva.setFilterCutoff(0.75);
~ibva.setSamplingRate(200);  //display capped at 60fps