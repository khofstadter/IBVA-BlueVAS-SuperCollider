//helper class for IBVArecorder and IBVAplayer

IBVAfile {
	*convert {|path, outPath|  //from binary to text file
		var dict= ();
		var file;
		dict= this.read(path);
		outPath= (outPath ?? {path++"CONVERTED.txt"}).standardizePath;
		file= File(outPath, "w");
		file.write(this.generateHeaderString(dict));
		dict.data.do{|val, i|
			if(i%4==3, {
				file.write("%\n".format(val));
			}, {
				file.write("% ".format(val));
			});
		};
		file.close;
		"wrote file %".format(outPath).postln;
	}
	*generateHeaderString {|ibva|
		^"ibva % ch 4 sr % fr %\n".format(ibva.version, ibva.sr, ibva.fr)
	}
	*read {|path, ibva|
		var file, dict= ();
		var filePath= path.standardizePath;
		if(File.exists(filePath), {
			file= File(filePath, "rb");
			dict.putAll(this.prReadHeader(file));
			dict.put(\data, this.prReadData(file));
			file.close;
		}, {
			"IBVAfile: file % not found".format(filePath).warn;
		});
		^dict;
	}

	//--private
	*prReadHeader {|file|
		var dict= ();
		var line= file.readUpTo($\n);
		if(line.beginsWith("ibva "), {
			line.split(Char.space).pairsDo{|k, v|
				dict.put(k.asSymbol, v.asFloat);
			};
		}, {
			"IBVAfile: wrong file format".warn;
		});
		^dict;
	}
	*prReadData {|file|
		var data= List.new;
		var val;
		while({val= file.getInt16; val.notNil and:{val!= -1}}, {
			data.add(val);
		});
		^data;
	}
}
