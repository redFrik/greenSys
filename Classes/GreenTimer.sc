//TODO cmd period survive?

GreenTimer {

	var <startTime;
	var stopped;

	*new {|alarm, action|
		^super.new.initGreenTimer(alarm, action)
	}

	*newPost {|interval= 1, formatted= false|
		^this.new(interval,
			if(formatted, {
				{|t| t.currentTimeHMS.postln; interval}
			}, {
				{|t| t.currentTime.postln; interval}
			})
		)
	}

	initGreenTimer {|alarm, action|
		stopped= false;
		startTime= SystemClock.seconds;
		SystemClock.sched(alarm, {
			if(stopped.not, {
				action.(this);
			}, {nil});
		});
	}

	currentTime {
		^SystemClock.seconds-startTime
	}

	currentTimeHMS {|decimalPlaces= 3|
		^this.currentTime.asTimeString(decimalPlaces: decimalPlaces)
	}

	stop {
		stopped= true;
	}
}
