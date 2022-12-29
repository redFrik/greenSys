//TODO GreenPeaceGUI - just buttons

//f0 2016
GreenPeace {
	classvar setup, synths;

	*initClass {
		ServerBoot.add({
			SynthDef(\greenpeace, {|in= 0, dur= 0.1|
				var src= InFeedback.ar(in, 1);
				var clip= 1-InRange.ar(src, -1, 1);
				SendReply.ar(Trig1.ar(clip, dur), '/greenpeace', [in, src]);
			}, #[\ir, \ir]).add;
		});
	}

	*activate {|bus= 0, numChannels= 2, dur= 0.1, target, func|
		target= target.asTarget;
		synths.do{|x| x.free};
		CmdPeriod.remove(setup);
		func= func ? {|msg| ("clip! bus: % val: %").format(msg[3], msg[4]).warn};
		fork{
			setup= {
				{
					synths= {|i|
						Synth(\greenpeace, [\in, bus+i, \dur, dur], target, \addToTail);
					}.dup(numChannels);
					OSCdef(\greenpeace, func, \greenpeace, Server.default.addr);
				}.defer(0.01);
			};
			setup.value;
			CmdPeriod.add(setup);
			"GreenPeace active".postln;
		};
	}

	*deactivate {
		synths.do{|x| x.free};
		CmdPeriod.remove(setup);
		"GreenPeace inactive".postln;
		OSCdef(\greenpeace).free;
	}

}
