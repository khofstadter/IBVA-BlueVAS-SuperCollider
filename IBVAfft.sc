//for collecting IBVA data and perform fft

IBVAfft {
	var <ibva, <data, <fft, func, table, imag, size;
	*new {|ibva|
		^super.new.initIBVAfft(ibva);
	}
	initIBVAfft {|argIbva|
		ibva= argIbva;
		this.prInit(ibva.sr ? 120);
		CmdPeriod.doOnce({this.stop});
	}
	start {
		"IBVAfft: started".postln;
		func= {|...args|
			if(ibva.sr.nextPowerOfTwo!=size, {
				"IBVAfft: samplerate changed - clearing".warn;
				this.prInit(ibva.sr);
			});
			data[0].pop;
			data[1].pop;
			data[2].pop;
			data[3].pop;
			data[0].insert(0, args[0]-511.5);
			data[1].insert(0, args[1]-511.5);
			data[2].insert(0, args[2]);
			data[3].insert(0, args[3]);
			fft[0]= fft(data[0], imag, table).magnitude.copyRange(0, size.div(2))*0.01;
			fft[1]= fft(data[1], imag, table).magnitude.copyRange(0, size.div(2))*0.01;
			fft[2]= fft(data[2], imag, table).magnitude.copyRange(0, size.div(2))*0.01;
			fft[3]= fft(data[3], imag, table).magnitude.copyRange(0, size.div(2))*0.01;
			fft;
		};
		ibva.action= ibva.action.addFunc(func);
	}
	stop {
		ibva.action= ibva.action.removeFunc(func);
		"IBVAfft: stopped".postln;
	}

	//--private
	prInit {|sr|
		size= sr.nextPowerOfTwo;
		table= Signal.fftCosTable(size);
		imag= Signal.newClear(size);
		data= {Signal.newClear(size)}.dup(4);
		fft= {Signal.newClear(size.div(2))}.dup(4);
	}
}
