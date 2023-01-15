//nanoKONTROL v1 (9 sliders & 9 knobs, scene #1, factory settings)

//TODO free on close?

//related: GreenNanoKontrol, GreenNanoKontrol2GUI

GreenNanoKontrolGUI : SCViewHolder {

	const <ctrlSymbols= #['<<', '>', '>>', 'o', '.', '•'];  //rew, play, ff, loop, stop, rec

	var <nanoKontrol;
	var focusActions;
	var withinAction;

	*new {|parent, bounds, nanoKontrol|
		^super.new.initGreenNanoKontrolGUI(parent, bounds, nanoKontrol)
	}

	initGreenNanoKontrolGUI {|parent, bounds, argNanoKontrol|
		var ctrl, knobs, sliders, buttons1, buttons2;

		var skin= GUI.skins.guiCV;
		var ctrlWidth= "<<".bounds(Font(*skin.fontSpecs)).width*2;

		nanoKontrol= argNanoKontrol;

		//--widgets
		ctrl= nanoKontrol.ctrl.collect{|cv, i|
			GUICVButton(ref: cv.ref, update:false)
			.maxWidth_(ctrlWidth)
			.string_(this.class.ctrlSymbols[i])
		};

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

		buttons1= nanoKontrol.buttons1.collect{|cv|
			GUICVButton(ref: cv.ref, update:false)
			.maxSize_(Size(20, 20))
		};

		buttons2= nanoKontrol.buttons2.collect{|cv|
			GUICVButton(ref: cv.ref, update:false)
			.maxSize_(Size(20, 20))
		};

		//--focus
		focusActions= [ctrl, knobs, sliders, buttons1, buttons2].flat.collect{|v|
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
					[View()],
					[[GUICV.staticText.string_("nanoKONTROL"), columns: 3, align: \center]],
					ctrl[#[0, 1, 2]],
					ctrl[#[3, 4, 5]],
					[View()]
				),
				skin.spacing,
				GridLayout.rows(
					knobs,
					sliders.collect{|slider, i|
						GridLayout.columns(
							[buttons1[i], buttons2[i]],
							[[slider, rows:2]]
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
			//nanoKontrol.responders.do{|x| x.free};  //TODO?
		});
	}
}
