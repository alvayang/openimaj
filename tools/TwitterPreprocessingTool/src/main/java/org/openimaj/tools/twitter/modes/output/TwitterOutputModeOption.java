/**
 * Copyright (c) 2012, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.tools.twitter.modes.output;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineOptionsProvider;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ProxyOptionHandler;
import org.openimaj.tools.InOutToolOptions;
import org.openimaj.tools.twitter.modes.preprocessing.TwitterPreprocessingModeOption;

/**
 * Control how twitter analysis should be outputted
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>, Sina Samangooei <ss@ecs.soton.ac.uk>
 *
 */
public enum TwitterOutputModeOption  implements CmdLineOptionsProvider {
	/**
	 * Appends the analysis to tweets
	 */
	APPEND {
		@Override
		public TwitterOutputMode createMode(List<TwitterPreprocessingModeOption> twitterPreprocessingModes) {
			return new SelectiveAnalysisOutputMode();
		}
	},
	/**
	 * analysis and JPATH specified optional extras
	 */
	CONDENSED {
		
		@Option(name="--twitter-extras", aliases="-te", required=false, usage="On top of the analysis, what should be saved. If none are specified id and created_at are saved.", multiValued=true)
		List<String> twitterExtras = new ArrayList<String>();
		
		@Override
		public TwitterOutputMode createMode(List<TwitterPreprocessingModeOption> twitterPreprocessingModes) {
			InOutToolOptions.prepareMultivaluedArgument(twitterExtras);
			if(twitterExtras.size() == 0){
				twitterExtras.add("id");
				twitterExtras.add("created_at");
			}
			List<String> analysisKeys = new ArrayList<String>();
			for (TwitterPreprocessingModeOption mode : twitterPreprocessingModes) {
				analysisKeys.add(mode.getAnalysisKey());
			}
			return new SelectiveAnalysisOutputMode(analysisKeys,twitterExtras);
		}
	},
	/**
	 * just the analysis, no tweet
	 */
	ANALYSIS {
		@Override
		public TwitterOutputMode createMode(List<TwitterPreprocessingModeOption> twitterPreprocessingModes) {
			List<String> analysisKeys = new ArrayList<String>();
			for (TwitterPreprocessingModeOption mode : twitterPreprocessingModes) {
				analysisKeys.add(mode.getAnalysisKey());
			}
			return new SelectiveAnalysisOutputMode(analysisKeys);
		}
	};
	
	@Override
	public Object getOptions() {return this;}

	/**
	 * @param twitterPreprocessingModes the processing modes
	 * @return an output mode
	 */
	public abstract TwitterOutputMode createMode(List<TwitterPreprocessingModeOption> twitterPreprocessingModes);	
}