//based on RedTest

GreenTest {

	classvar player;

	*speaker {|channels= #[0, 1], amp= 0.5, dur= 1|
		Routine.run{
			Server.default.bootSync;
			SynthDef(\greenTestPink, {|out= 0, gate= 1, amp= 1|
				var e= EnvGen.kr(Env.perc, gate, doneAction:2);
				var z= PinkNoise.ar(e*amp);
				Out.ar(out, z);
			}).add;
			Server.default.sync;
			player.stop;
			player= Pbind(
				\instrument, \greenTestPink,
				\dur, dur,
				\out, Pseq(channels, inf),
				\amp, amp
			).play;
		};
	}

	*speaker2 {|channels= #[0, 1], amp= 0.5, dur= 1|
		Routine.run{
			Server.default.bootSync;
			SynthDef(\greenTestPing, {|out= 0, gate= 1, freq= 400, amp= 1|
				var e= EnvGen.kr(Env.perc, gate, doneAction:2);
				var z= SinOsc.ar(freq, 0, e*amp);
				Out.ar(out, z);
			}).add;
			Server.default.sync;
			player= Pbind(
				\instrument, \greenTestPing,
				\dur, dur,
				\out, Pseq(channels, inf),
				\degree, Pseq(channels, inf),
				\amp, amp
			).play;
		};
	}

	*stop {
		player.stop;
	}
}
