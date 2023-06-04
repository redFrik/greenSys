//related: GreenSoundFile

GreenBuffer {

	classvar <>defaultFolderPath;  //preferably set in startup.scd

	*new {|server, numChannels= 1, path, recursive= false, exclude= #[]|
		^this.prBufferFromGreenSoundFile(\new, server, path, numChannels, recursive, exclude)
	}

	*next {|server, numChannels= 1, path, recursive= false, exclude= #[]|
		^this.prBufferFromGreenSoundFile(\next, server, path, numChannels, recursive, exclude)
	}

	*prev {|server, numChannels= 1, path, recursive= false, exclude= #[]|
		^this.prBufferFromGreenSoundFile(\prev, server, path, numChannels, recursive, exclude)
	}

	*random {|server, numChannels= 1, path, recursive= false, exclude= #[]|
		^this.prBufferFromGreenSoundFile(\random, server, path, numChannels, recursive, exclude)
	}

	*randomReplace {|buffer, path, recursive= false, exclude= #[]|
		var file;
		if(buffer.isKindOf(Buffer).not, {
			"GreenBuffer: first argument must be a Buffer instance".warn;
			^nil
		});
		exclude= exclude++[buffer.path.basename];
		path= path??{buffer.path.dirname};  //try within the same folder
		file= GreenSoundFile.random(path, buffer.numChannels, nil, nil, recursive, exclude);
		if(file.isNil, {^nil});
		^buffer.read(file.path)
	}

	*randomCached {|server, numChannels= 1, exclude= #[]|
		var buffer, buffers= List.new;
		Buffer.cachedBuffersDo(server??{Server.default}, {|b|
			if(b.numChannels==numChannels and:{GreenString.match(b.path.basename, exclude).not}, {
				buffers.add(b)
			})
		});
		if(buffers.isEmpty, {
			"GreenBuffer: no % channel buffer found in cache".format(numChannels).warn
			^nil
		});
		^buffers.choose
	}

	//--private

	*prBufferFromGreenSoundFile {|method, server, path, channels, recursive, exclude|
		var file;
		path= path??{this.defaultFolderPath??{Platform.resourceDir+/+"sounds"}};
		file= GreenSoundFile.perform(method, path, channels, nil, nil, recursive, exclude);
		if(file.isNil, {^nil});
		^Buffer.read(server??{Server.default}, file.path)
	}
}
