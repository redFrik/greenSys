//related: GreenPeaceGUI

//f0 2016 - reworked 2022

GreenPeace {

	classvar startFunc, synths, bbus, nnum, ddur, ttarget, aaction;

	*initClass {
		SynthDef(\greenpeace, {|bus= 0, dur= 0.1|
			var src= In.ar(bus, 1);
			var clip= 1-InRange.ar(src, -1, 1);
			SendReply.ar(Trig1.ar(clip, dur), '/greenpeace', [bus, src]);
		}, #[\ir, \ir]).add;

		startFunc= {
			synths= {|i|
				Synth(\greenpeace, [\bus, bbus+i, \dur, ddur], ttarget, \addAfter);
			}.dup(nnum);
			OSCdef(\greenpeace, aaction, \greenpeace, ttarget.server.addr);
			"GreenPeace active on bus: %, numCh: %.".format(bbus, nnum).postln;
		};
	}

	*activate {|bus= 0, numChannels= 2, dur= 0.1, target, action|
		bbus= bus;
		nnum= numChannels;
		ddur= dur;
		ttarget= target.asTarget;
		aaction= action ? {|msg| "clip! bus: % val: %".format(msg[3].asInteger, msg[4]).warn};

		synths.do{|x| x.free};
		ServerTree.add(startFunc, ttarget.server);
		if(ttarget.server.serverRunning, {
			startFunc.value;
		});
	}

	*deactivate {
		synths.do{|x| x.free};
		synths= nil;
		ServerTree.remove(startFunc, ttarget.server);
		"GreenPeace inactive".postln;
		OSCdef(\greenpeace).free;
	}

}
