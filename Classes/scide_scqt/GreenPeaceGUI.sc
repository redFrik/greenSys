//TODO fix so that several can run at the same time

//related: GreenPeace

GreenPeaceGUI : SCViewHolder {

	var buttons;

	*new {|parent, bounds, bus= 0, numChannels= 2, dur= 0.1, target, action|
		^super.new.initGreenPeaceGUI(parent, bounds, bus, numChannels, dur, target, action)
	}

	initGreenPeaceGUI {|parent, bounds, bus, numChannels, dur, target, action|
		var skin= GUI.skins.guiCV;

		var func= {|msg|
			var chan= msg[3].asInteger;
			var peak= GUICV.fixDec(msg[4], 2);
			var but= buttons[chan];
			{
				but.states_([but.states[0], [peak, skin.background, skin.highlight]]);
				but.value= 1;
			}.defer;
		};
		GreenPeace.activate(bus, numChannels, dur, target, func.addFunc(action));

		buttons= {|i|
			GUICV.button
			.action_({|v, mod|
				if(mod.isShift, {this.resetPeaks});
				v.value= 0;
			})
			.canFocus_(false)
			.minSize_(Size(skin.knobWidth, skin.buttonHeight))
			.states_([[bus+i, skin.fontColor, skin.foreground], ["", skin.background, skin.highlight]])
		}.dup(numChannels);

		view= View(parent, bounds).layout_(
			HLayout(
				*buttons
			).margins_(0).spacing_(0)
		)
		.background_(skin.background)
		.onClose_({GreenPeace.deactivate});

		if(bounds.isNil, {view.front});
	}

	resetPeaks {
		buttons.do{|but| but.value= 0};
	}
}
