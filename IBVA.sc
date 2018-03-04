//--supercollider BlueVAS communication

IBVA {
	var <port, <>action, <>data, task, <>sr, <fr, <version= 1.7,
	<>mute= false, >ovAction, >blAction, >dnAction, <>srAction, >frAction;
	*new {|port, action, rate= 120, cutoff= 0.3|
		^super.new.initIBVA(port, action, rate, cutoff);
	}
	initIBVA {|argPort, argAction, rate, cutoff|
		data= [-1, -1, -1, -1];
		port= SerialPort(argPort ? "/dev/tty.BlueVAS_H-Data", 230400, crtscts:true);
		CmdPeriod.doOnce({port.close});

		//--default actions
		action= argAction;
		ovAction= {|val| "number of missing samples: %".format(val).postln};
		blAction= {|val| "battery level: %V".format(val).postln};
		dnAction= {|str| "device name: %".format(str).postln};
		srAction= {|val| "sampling rate: %".format(val).postln};
		frAction= {|val| "filter cutoff frequency ratio: %".format(val).postln};

		//--read loop
		task= Routine({
			var line= "";
			this.setSamplingRate(rate);
			this.setFilterCutoff(cutoff);
			inf.do{
				var byte= port.read;
				//byte.postln;  //debug
				if(mute.not, {
					if(byte!=13, {
						line= line++byte.asAscii;  //collect characters into a string
					}, {
						case
						{line[0]==$o and:{line[1]==$v}} {ovAction.value(line[3..].asInteger)}
						{line[0]==$b and:{line[1]==$l}} {blAction.value(this.prHexStrToInt(line[3..])/1024*16)}
						{line[0]==$d and:{line[1]==$n}} {dnAction.value(line[3..])}
						{line[0]==$s and:{line[1]==$r}} {srAction.value(line[3..].asInteger)}
						{line[0]==$f and:{line[1]==$r}} {frAction.value(line[3..].asFloat)}
						{
							data= line.split(Char.tab).collect{|x| this.prHexStrToInt(x)};
							action.value(*data);
						};
						line= "";
					});
				});
			};
		}).play(SystemClock);
	}
	close {
		task.stop;
		port.close;
	}

	//--asynchronous
	getBatteryLevel {port.putAll("BL\r\n")}
	getDeviceName {port.putAll("DN\r\n")}
	getSamplingRate {port.putAll("SR\r\n")}
	getFilterCutoff {port.putAll("FR\r\n")}

	//--setters
	setSamplingRate {|rate= 1|
		var val= rate.asInteger.clip(1, 2000);
		port.putAll("SR "++val++"\r\n");
		sr= val;
	}
	setFilterCutoff {|ratio= 0.3333|
		var val= ratio.clip(0.1, 1);
		var arr= val.asString.findRegexp("\\d+").flop[1].extend(2, "0");
		val= arr[0]++$.++arr[1].padRight(3, "0")[0..2];
		port.putAll("FR "++val++"\r\n");
		fr= val;
	}

	//--private
	prHexStrToInt {|str|
		^str.sum{|chr, i| chr.digit*(16**(str.size-1-i))}.asInteger;
	}
}
