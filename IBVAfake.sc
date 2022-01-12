//--fake supercollider BlueVAS communication

//NOTE: just for testing without hardware. filters will not work

IBVAfake : IBVA {

	initIBVA {|argPort, argAction, rate, cutoff|
		data= [-1, -1, -1, -1];

		//--default actions
		action= argAction;
		ovAction= {|val| "number of missing samples: %".format(val).postln};
		blAction= {|val| "battery level: %V".format(val).postln};
		dnAction= {|str| "device name: %".format(str).postln};
		srAction= {|val| "sampling rate: %".format(val).postln};
		frAction= {|val| "filter cutoff frequency ratio: %".format(val).postln};

		//--read loop
		task= Routine({
			this.setSamplingRate(rate);
			this.setFilterCutoff(cutoff);
			data= {1024.rand}!4;  //fill with random data
			inf.do{
				if(mute.not, {
					data= (data+({0.gauss(10)}!4)).asInteger.clip(0, 1023);
					action.value(*data);
				});
				(1/sr).wait;
			};
		}).play(SystemClock);
	}
	close {
		task.stop;
	}

	getBatteryLevel {blAction.value(3.3)}
	getDeviceName {dnAction.value("fake")}
	getSamplingRate {srAction.value(sr)}
	getFilterCutoff {frAction.value(fr)}

	setSamplingRate {|rate= 1|
		var val= rate.asInteger.clip(1, 2000);
		sr= val;
	}
	setFilterCutoff {|ratio= 0.3333|
		var val= ratio.clip(0.1, 1);
		var arr= val.asString.findRegexp("\\d+").flop[1].extend(2, "0");
		val= arr[0]++$.++arr[1].padRight(3, "0")[0..2];
		fr= val;
	}
}
