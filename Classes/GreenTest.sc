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

	*speaker3 {|channels= #[0, 1], amp= 0.5, dur= 1|
		Routine.run{
			Server.default.bootSync;
			SynthDef(\greenTestImpulse, {|out= 0, amp= 1|
				var e= Line.ar(1, 0, 0.01, doneAction:2);
				var z= Impulse.ar(amp);
				OffsetOut.ar(out, z);
			}).add;
			Server.default.sync;
			player= Pbind(
				\instrument, \greenTestImpulse,
				\dur, dur,
				\out, Pseq(channels, inf),
				\amp, amp
			).play;
		};
	}

	*stop {
		player.stop;
	}


	//--WARNING these methods for debugging only

	*denormals {|channels= #[0, 1], target, addAction= \addToHead|
		Routine.run{
			Server.default.bootSync;
			SynthDef(\greenTestDenormals, {|out= 0|
				var e= EnvGate();
				var z= SinOsc.ar(50, 0, 10);
				z= (z*z).neg.exp;
				Out.ar(out, z);
			}).add;
			Server.default.sync;
			target= target??{Server.default.defaultGroup};
			player.stop;
			player= Pbind(
				\addAction, addAction,
				\instrument, \greenTestDenormals,
				\dur, 2,
				\legato, 0.5,
				\out, Pseq(channels, inf),
				\group, target
			).play;
		};
	}

	*infinity {|channels= #[0, 1], target, addAction= \addToHead|
		Routine.run{
			Server.default.bootSync;
			SynthDef(\greenTestInfinity, {|out= 0|
				var e= EnvGate();
				var z= DC.ar(1/0);
				Out.ar(out, z);
			}).add;
			Server.default.sync;
			target= target??{Server.default.defaultGroup};
			player.stop;
			player= Pbind(
				\addAction, addAction,
				\instrument, \greenTestInfinity,
				\dur, 2,
				\legato, 0.5,
				\out, Pseq(channels, inf),
				\group, target
			).play;
		};
	}

	*notANumber {|channels= #[0, 1], target, addAction= \addToHead|
		Routine.run{
			Server.default.bootSync;
			SynthDef(\greenTestNotANumber, {|out= 0, a= 0.1|
				var e= EnvGate();
				var z= PinkNoise.ar(a);
				Out.ar(out, z);
			}).add;
			Server.default.sync;
			target= target??{Server.default.defaultGroup};
			player.stop;
			player= Pbind(
				\a, -1.sqrt,  //NaN
				\addAction, addAction,
				\instrument, \greenTestNotANumber,
				\dur, 2,
				\legato, 0.5,
				\out, Pseq(channels, inf),
				\group, target
			).play;
		};
	}
}
