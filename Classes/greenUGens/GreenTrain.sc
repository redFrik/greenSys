//impulse train with curvature

GreenTrain {
	*ar {|
		trig,
		dur = 1,
		cur = 0,
		num = 8,
		amp = 1|
		var env = EnvGen.ar(Env(#[0, 0, 1], [0, dur], cur), trig);
		var train = Changed.ar(ceil(env * num)) * amp;
		// train = train * (1 - env); // optional decreasing amplitude
		// train = train * env; // optional - increasing amplitude
		^train
	}
	*kr {|
		trig,
		dur = 1,
		cur = 0,
		num = 8,
		amp = 1|
		var env = EnvGen.kr(Env(#[0, 0, 1], [0, dur], cur), trig);
		var train = Changed.kr(ceil(env * num)) * amp;
		// train = train * (1 - env); // optional decreasing amplitude
		// train = train * env; // optional - increasing amplitude
		^train
	}
}
