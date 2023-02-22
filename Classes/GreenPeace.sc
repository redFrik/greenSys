//related: GreenPeaceGUI

//f0 2016 - reworked 2022

GreenPeace {

	classvar responder, synths, makeSynths, server, abus, num;

	*initClass {
		StartUp.add({
			SynthDef(\greenPeace, {|bus= 0, dur= 0.1|
				var src= In.ar(bus, 1);
				var clip= 1-InRange.ar(src, -1, 1);
				SendReply.ar(Trig1.ar(clip, dur), '/greenPeace', [bus, src]);
			}, #[\ir, \ir]).add;
		});
	}

	*activate {|bus= 0, numChannels= 2, dur= 0.1, target, action|
		target= target.asTarget;
		server= target.server;
		abus= bus;
		num= numChannels;
		synths.do{|x| x.free};
		makeSynths= {
			{|i| Synth(\greenPeace, [\bus, bus+i, \dur, dur], target, \addAfter)}.dup(num)
		};
		action= action ? {|msg| "clip! bus: % val: %".format(msg[3].asInteger, msg[4]).warn};
		responder.free;
		responder= OSCFunc(action, \greenPeace, server.addr).permanent_(true);
		ServerQuit.add(this, server);
		ServerTree.add(this, server);
		if(server.serverRunning, {
			this.doOnServerTree(server);
		});
	}

	*deactivate {
		responder.free;
		synths.do{|x| x.free};
		synths= nil;
		ServerQuit.remove(this, server);
		ServerTree.remove(this, server);
		"GreenPeace inactive".postln;
	}

	*doOnServerTree {|server|
		responder.enable;
		synths= makeSynths.value;
		"GreenPeace active on bus: %, numCh: %".format(abus, num).postln;
	}

	*doOnServerQuit {|server|
		responder.disable;
	}
}
