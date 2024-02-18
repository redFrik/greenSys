AbstractGreenMIDIController {

	var <cvs;
	var <>debug= false;
	var <>within;
	var <>withinAction;
	var device;
	var responders;

	*new {|within= 0.1|
		^super.new.initAbstractGreenMIDIControl(within)
	}

	*deviceName {^this.subclassResponsibility(thisMethod)}

	initAbstractGreenMIDIControl {|argWithin|
		cvs= ();
		within= argWithin;
		responders= List.new;

		if(MIDIClient.initialized.not, {
			MIDIClient.init;
		});

		MIDIClient.sources.do{|src, i|
			if(src.device==this.class.deviceName, {
				device= src;
				MIDIIn.connect(i, src);
				"%: connected to % [%]".format(this.class.name, src.device, src.uid).postln;
			});
		};
		if(device.isNil, {
			"%: device % not found".format(this.class.name, this.class.deviceName).warn;
		});

		this.prSetup;
	}

	gui {|position| ^this.subclassResponsibility(thisMethod)}

	free {
		responders.do{|x| x.free};
		cvs.do{|cv| cv.remove};
	}

	//--private

	prSetup {^this.subclassResponsibility(thisMethod)}

	prSetupCC {|key, method, cc|
		var res, lastRes;
		var cv= CV();
		cvs.put(key, cv);
		this.addUniqueMethod(key, {|obj| cv});

		if(device.notNil, {
			responders.add(
				MIDIFunc.cc({|val, num|
					{
						res= cv.perform(method, val/127, within);  //set or softSet

						if(res.isNumber.not and:{lastRes!=res}, {
							lastRes= res;
							withinAction.value(key, cv, res);
						});

						if(debug, {
							"%: #% % %".format(key, num, val, cv).postln;
						});
					}.defer;
				}, cc, srcID: device.uid).permanent_(true)
			);
		});
		^cv
	}
}
