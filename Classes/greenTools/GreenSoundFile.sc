//related: GreenBuffer

GreenSoundFile {

	classvar <>defaultFolderPath;  //preferably set in startup.scd
	classvar <>lastSoundFile;  //used for xrand, next, prev

	*initClass {
		StartUp.add({
			lastSoundFile= this.new(defaultFolderPath)
		})
	}

	*new {|path, channels, minDur, maxDur, recursive= false, exclude= #[]|
		^this.random(path, channels, minDur, maxDur, recursive, exclude)
	}

	*next {|path, channels, minDur, maxDur, recursive= false, exclude= #[]|
		var all= this.all(path, channels, minDur, maxDur, recursive);
		^lastSoundFile= all.wrapAt(all.detectIndex{|f| f.path==lastSoundFile.path}?0+1)
	}

	*prev {|path, channels, minDur, maxDur, recursive= false, exclude= #[]|
		var all= this.all(path, channels, minDur, maxDur, recursive);
		^lastSoundFile= all.wrapAt(all.detectIndex{|f| f.path==lastSoundFile.path}?0-1)
	}

	*random {|path, channels, minDur, maxDur, recursive= false, exclude= #[]|
		^lastSoundFile= this.all(path, channels, minDur, maxDur, recursive, exclude)
		.reject{|f| f==lastSoundFile}.choose
	}

	*all {|path, channels, minDur, maxDur, recursive= false, exclude= #[]|
		var all= List.new;
		path= path??{this.defaultFolderPath??{Platform.resourceDir+/+"sounds"}};
		if(recursive, {
			PathName(path).filesDo{|p|
				var f= SoundFile(p.fullPath).info;
				if(f.notNil, {all.add(f)});
			};
		}, {
			if(path.endsWith("*").not, {path= path+/+"*"});
			all= SoundFile.collect(path);
		});
		if(all.size==0, {
			"GreenSoundFile: no files found in %".format(path).warn;
		});
		if(channels.notNil, {
			channels= channels.asArray;
			all= all.select{|x| channels.includes(x.numChannels)};
		});
		if(minDur.notNil, {
			all= all.select{|x| x.duration>=minDur};
		});
		if(maxDur.notNil, {
			all= all.select{|x| x.duration<=maxDur};
		});
		if(exclude.size>0, {
			all= all.reject{|x| GreenString.match(x.path, exclude)};
		});
		^all
	}
}
