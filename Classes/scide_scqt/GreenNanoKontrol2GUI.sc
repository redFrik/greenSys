//nanoKONTROL v2 (8 sliders & 8 knobs, scene #1, factory settings)

//related: GreenNanoKontrol2, GreenNanoKontrolGUI

GreenNanoKontrol2GUI : SCViewHolder {

	const <ctrlSymbols= #['cyc', '<<', '>>', '.', '>', '•', '<', '>', 'set', '<', '>'];
	//\cycle, \rew, \ff, \stop, \play, \rec, \trkDec, \trkInc, \mrkSet, \mrkDec, \mrkInc

	var <nanoKontrol;
	var focusActions;
	var withinAction;

	*new {|parent, bounds, nanoKontrol|
		^super.new.initGreenNanoKontrol2GUI(parent, bounds, nanoKontrol)
	}

	initGreenNanoKontrol2GUI {|parent, bounds, argNanoKontrol|
		var ctrl, knobs, sliders, buttonsS, buttonsM, buttonsR;

		var skin= GUI.skins.guiCV;
		var ctrlWidth= "cyc".bounds(Font(*skin.fontSpecs)).width*1.5;

		nanoKontrol= argNanoKontrol;

		//--widgets
		ctrl= nanoKontrol.ctrl.collect{|cv, i|
			GUICVButton(ref: cv.ref, update:false)
			.maxSize_(Size(ctrlWidth, skin.buttonHeight))
			.string_(this.class.ctrlSymbols[i])
		};
		ctrl[1..5].do{|b| b.maxHeight_(ctrlWidth)};

		knobs= nanoKontrol.knobs.collect{|cv|
			GUICVKnob(ref: cv.ref, update:false)
			.maxSize_(Size(skin.knobWidth, skin.knobWidth))
		};

		sliders= nanoKontrol.sliders.collect{|cv, i|
			GUICVSliderLabel(ref: cv.ref, update:false)
			.knobColor_(skin.highlight)
			.minSize_(Size(skin.sliderWidth, skin.sliderHeight))
			.string_((i+1).asString)
			.stringRotation_(0.5pi)
		};

		buttonsS= nanoKontrol.buttonsS.collect{|cv|
			GUICVButton(ref: cv.ref, update:false)
			.maxSize_(Size(20, 20))
			.string_("S")
		};

		buttonsM= nanoKontrol.buttonsM.collect{|cv|
			GUICVButton(ref: cv.ref, update:false)
			.maxSize_(Size(20, 20))
			.string_("M")
		};

		buttonsR= nanoKontrol.buttonsR.collect{|cv|
			GUICVButton(ref: cv.ref, update:false)
			.maxSize_(Size(20, 20))
			.string_("R")
		};

		//--focus
		focusActions= [ctrl, knobs, sliders, buttonsS, buttonsM, buttonsR].flat.collect{|v|
			var action= {if(v.hasFocus.not, {v.focus})};
			v.cv.addAction(action);
			[v.cv, action]
		};

		//--softSet
		withinAction= {|key, cv, bool|
			if(knobs.any{|knob|
				var match= knob.cv.ref==cv.ref;
				if(match, {
					knob.valueColor_(if(bool, {skin.highlight}, {skin.foreground}));
				});
				match
			}.not, {
				sliders.any{|slider|
					var match= slider.cv.ref==cv.ref;
					if(match, {
						slider.fillColor_(if(bool, {skin.highlight}, {skin.foreground}))
						.refresh;
					});
					match
				};
			});
		};
		nanoKontrol.withinAction_(nanoKontrol.withinAction.addFunc(withinAction));

		//--view
		view= View(parent, bounds).layout_(
			HLayout(
				GridLayout.rows(
					[[GUICV.staticText.string_("nanoKONTROL2"), columns: 5, align: \center]],
					[ctrl[6], ctrl[7], nil, nil, nil],
					[ctrl[0], nil, ctrl[8], ctrl[9], ctrl[10]],
					[ctrl[1], ctrl[2], ctrl[3], ctrl[4], ctrl[5]],
				),
				skin.spacing,
				GridLayout.rows(
					knobs,
					sliders.collect{|slider, i|
						GridLayout.columns(
							[buttonsS[i], buttonsM[i], buttonsR[i]],
							[[slider, rows:3]]
						)
					}
				)
			)
		)
		.background_(skin.background);

		if(bounds.isNil, {
			view
			.moveTo(300, Window.screenBounds.height*0.75)
			.front;
		});

		view.onClose_({
			focusActions.do{|arr| arr[0].removeAction(arr[1])};
			nanoKontrol.withinAction.removeFunc(withinAction);
			//nanoKontrol.responders.do{|x| x.free};  //TODO
		});
	}
}
