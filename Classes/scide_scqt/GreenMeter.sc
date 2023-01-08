//related: GreenServerMeter

GreenMeter : SCViewHolder {

	var <>dBLow= -80;
	var texts, meters, buttons, maxPeaks;
	var responder, synth, makeSynth;

	*new {|parent, bounds, target, addAction= \addToTail, index= 0, numChannels= 2, rate= 30|
		^super.new.initGreenMeter(parent, bounds, target.asTarget, addAction, index, numChannels, rate)
	}

	initGreenMeter {|parent, bounds, target, addAction, index, numChannels, rate|

		var skin= GUI.skins.guiCV;
		var font= Font(*skin.fontSpecs);
		var fontSmall= Font(*skin.fontSmallSpecs);
		var fontHeight= "".bounds(font).height;
		var minWidth= "-99".bounds(font).width;

		var id, lastPeaks, lastRMSs;

		texts= {
			GUICV.staticText.string_("-∞")
			.align_(\center)
			.fixedHeight_(fontHeight)
			.minWidth_(minWidth)
		}.dup(numChannels);

		meters= {
			GUICV.levelIndicator
			.minHeight_(fontHeight)
		}.dup(numChannels);

		buttons= {|i|
			GUICV.button
			.action_({|v, mod|
				if(mod.isShift, {
					this.resetPeaks;
				}, {
					v.value= 0;
					maxPeaks[i]= -inf;
				});
			})
			.canFocus_(false)
			.font_(fontSmall)
			.fixedHeight_(fontHeight)
			.minWidth_(minWidth)
			.states_([[" ", skin.background, skin.background]])
		}.dup(numChannels);

		view= View(parent, bounds).layout_(
			GridLayout.rows(buttons, meters, texts).margins_(0).spacing_(1)
		)
		.background_(skin.background);

		if(bounds.isNil, {
			view.resizeTo((minWidth+1)*numChannels, (fontHeight*2)+skin.sliderHeight)
			.moveTo(0, Window.screenBounds.height*0.85)
			.name_(this.class.name)
			.front;
		});

		id= UniqueID.next;
		lastPeaks= 0.dup(numChannels);
		lastRMSs= 0.dup(numChannels);
		maxPeaks= -inf.dup(numChannels);

		responder= OSCFunc({|msg|
			numChannels.do{|i|
				var changed= false;
				var peak= msg[i*2+3].ampdb.max(dBLow);
				var rms= msg[i*2+4].ampdb.asInteger;
				if(peak>0, {
					if(peak>maxPeaks[i], {
						maxPeaks[i]= peak;
						{
							buttons[i].states_([
								buttons[i].states[0],
								[GUICV.fixDec(maxPeaks[i], 1), skin.background, skin.fontColor]
							]).value_(1);
						}.defer;
					});
				});
				peak= peak.round.asInteger;
				if(peak!=lastPeaks[i], {
					changed= true;
					lastPeaks[i]= peak;
				});
				if(rms!=lastRMSs[i], {
					changed= true;
					lastRMSs[i]= rms;
				});
				if(changed, {
					{
						meters[i].peakLevel= peak.linlin(dBLow, 0, 0, 1);
						meters[i].value= rms.linlin(dBLow, 0, 0, 1);
						texts[i].string_(if(peak>dBLow, {peak}, {"-∞"}));
					}.defer;
				});
			};
		}, "greenMeter"++id, target.server.addr).permanent_(true).disable;

		makeSynth= {
			SynthDef("greenMeter_%__%".format(numChannels, id).asSymbol, {
				var src= In.ar(index, numChannels);
				SendPeakRMS.kr(src, rate, 3, "/greenMeter"++id);
			}).play(target, addAction: addAction);
		};

		ServerQuit.add(this, target.server);
		ServerTree.add(this, target.server);
		if(target.server.serverRunning, {
			this.doOnServerTree;
		});

		view.onClose_({
			responder.free;
			synth.free;
			ServerQuit.remove(this, target.server);
			ServerTree.remove(this, target.server);
		});
	}

	resetPeaks {
		buttons.do{|b| b.value= 0};
		maxPeaks= -inf.dup(buttons.size);
	}

	doOnServerTree {|server|
		responder.enable;
		synth= makeSynth.value;
	}

	doOnServerQuit {|server|
		responder.disable;
		meters.do{|m| m.peakLevel= 0; m.value= 0};
		texts.do{|t| t.string_("-∞")};
		this.resetPeaks;
	}
}
