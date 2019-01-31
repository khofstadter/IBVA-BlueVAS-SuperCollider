//for recording IBVA data to disk

IBVArecorder {
	var <ibva, file, filePath, func;
	*new {|ibva|
		^super.new.initIBVArecorder(ibva);
	}
	initIBVArecorder {|argIbva|
		ibva= argIbva;
	}
	prepare {|path|
		filePath= path.standardizePath;
		if(file.notNil, {file.close});  //safety - close previous
		file= File(filePath, "wb");
		file.write(IBVAfile.generateHeaderString(ibva));
		CmdPeriod.doOnce({this.stop});
	}
	record {
		if(file.notNil and:{file.isOpen}, {
			"%: recording to %".format(this.class.name, filePath).postln;
			func= {|...args| file.write(Int16Array.newFrom(args))};
			ibva.action= ibva.action.addFunc(func);
		}, {
			"%: file not open - .prepare first".format(this.class.name).warn;
		});
	}
	stop {
		file.close;
		ibva.action= ibva.action.removeFunc(func);
		"%: stopped".format(this.class.name).postln;
	}
}
