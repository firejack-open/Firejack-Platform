/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

tinyMCEPopup.requireLangPack();

var MergeCellsDialog = {
	init : function() {
		var f = document.forms[0];

		f.numcols.value = tinyMCEPopup.getWindowArg('cols', 1);
		f.numrows.value = tinyMCEPopup.getWindowArg('rows', 1);
	},

	merge : function() {
		var func, f = document.forms[0];

		tinyMCEPopup.restoreSelection();

		func = tinyMCEPopup.getWindowArg('onaction');

		func({
			cols : f.numcols.value,
			rows : f.numrows.value
		});

		tinyMCEPopup.close();
	}
};

tinyMCEPopup.onInit.add(MergeCellsDialog.init, MergeCellsDialog);
