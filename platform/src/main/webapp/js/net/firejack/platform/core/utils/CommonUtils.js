//@require PrototypeUtils.js
/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

OPF.capitalise = function(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
};

OPF.isNotEmpty = function(obj) {
    return obj != undefined && obj != null;
};

OPF.isEmpty = function(obj) {
    return obj == undefined || obj == null;
};

OPF.isBlank = function(str) {
    return OPF.isEmpty(str) || str.length == 0
};

OPF.isNotBlank = function(str) {
    return OPF.isNotEmpty(str) && str.length > 0;
};

OPF.ifBlank = function(str, defaultValue) {
    if (OPF.isNotBlank(str)) {
        return str;
    }
    return defaultValue;
};

OPF.ifEmpty = function(value, defaultValue) {
    if (OPF.isNotEmpty(value)) {
        return value;
    }
    return defaultValue;
};

OPF.getSimpleClassName = function(fullClassName) {
    var lastDotPos = fullClassName.lastIndexOf('.');
    return fullClassName.substring(lastDotPos + 1);
};

OPF.findPackageLookup = function(lookup) {
    var pattern = /^[\w\-]+\.[\w\-]+\.[\w\-]+/g;
    return pattern.exec(lookup)[0];
};

OPF.findPathFromLookup = function(lookup) {
    return lookup.substring(0, lookup.lastIndexOf('.'));
};

OPF.generateUrlByLookup = function(lookup, prefixUrl) {
    var packageLookup = OPF.findPackageLookup(lookup);
    var lookupSuffix = lookup.substring(packageLookup.length);
    var urlSuffix = lookupSuffix.replace(/\./g, '/');
    return OPF.Cfg.fullUrl((prefixUrl || '') + urlSuffix, true);
};

OPF.cutting = function(s, length) {
    if (OPF.isNotEmpty(s)) {
        if (s.length > length) {
            s = s.substring(0, length - 3) + '...';
        }
    }
    return s;
};

OPF.convertTime = function(value) {
    if (Ext.isNumeric(value)) {
        var datetime = new Date();
        datetime.setTime(value);
        value = Ext.Date.format(datetime, 'g:i A');
    }
    return value;
};

OPF.convertDate = function(value) {
    if (Ext.isNumeric(value)) {
        var datetime = new Date();
        datetime.setTime(value);
        value = Ext.Date.format(datetime, 'Y-m-d');
    }
    return value;
};

OPF.convertDatetime = function(value) {
    if (Ext.isNumeric(value)) {
        var datetime = new Date();
        datetime.setTime(value);
        value = Ext.Date.format(datetime, 'Y-m-d g:i:s A');
    }
    return value;
};

OPF.calculateColumnWidth = function(name) {
    return name.length * 8 + 25;
};

OPF.getQueryParam = function gup(name) {
    name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(window.location.href);
    if (results == null)
        return null;
    else
        return results[1];
};

OPF.copyStore = function(store, filterFn) {
	var records = [];
	var newStore = new Ext.data.ArrayStore({
		model : store.model
    });
	store.each(function (r) {
        if (filterFn(r)) {
            records.push (r.copy());
        }
	});
	newStore.loadRecords(records);
	return newStore;
};

OPF.loadJsFile = function(filename) {
    var fileref = document.createElement('script');
    fileref.setAttribute("type", "text/javascript");
    fileref.setAttribute("src", filename);
    document.getElementsByTagName("head")[0].appendChild(fileref);
};

OPF.loadCssFile = function(filename) {
    var fileref = document.createElement("link");
    fileref.setAttribute("rel", "stylesheet");
    fileref.setAttribute("type", "text/css");
    fileref.setAttribute("href", filename);
    document.getElementsByTagName("head")[0].appendChild(fileref)
};

OPF.windowSize = function() {
    var width = 0, height = 0;
    if (typeof( window.innerWidth ) == 'number') {
        //Non-IE
        width = window.innerWidth;
        height = window.innerHeight;
    } else if (document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight )) {
        //IE 6+ in 'standards compliant mode'
        width = document.documentElement.clientWidth;
        height = document.documentElement.clientHeight;
    } else if (document.body && ( document.body.clientWidth || document.body.clientHeight )) {
        //IE 4 compatible
        width = document.body.clientWidth;
        height = document.body.clientHeight;
    }
    return {
        width: width,
        height: height
    };
};

OPF.getScrollXY = function() {
    var scrOfX = 0, scrOfY = 0;
    if (typeof( window.pageYOffset ) == 'number') {
        //Netscape compliant
        scrOfY = window.pageYOffset;
        scrOfX = window.pageXOffset;
    } else if (document.body && ( document.body.scrollLeft || document.body.scrollTop )) {
        //DOM compliant
        scrOfY = document.body.scrollTop;
        scrOfX = document.body.scrollLeft;
    } else if (document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop )) {
        //IE6 standards compliant mode
        scrOfY = document.documentElement.scrollTop;
        scrOfX = document.documentElement.scrollLeft;
    }
    return {
        scrOfX: scrOfX,
        scrOfY: scrOfY
    };
};

OPF.randomUUID = function() {
  var s = [], itoh = '0123456789ABCDEF';

  // Make array of random hex digits. The UUID only has 32 digits in it, but we
  // allocate an extra items to make room for the '-'s we'll be inserting.
  for (var i = 0; i <36; i++) s[i] = Math.floor(Math.random()*0x10);

  // Conform to RFC-4122, section 4.4
  s[14] = 4;  // Set 4 high bits of time_high field to version
  s[19] = (s[19] & 0x3) | 0x8;  // Specify 2 high bits of clock sequence

  // Convert to hex chars
  for (var i = 0; i <36; i++) s[i] = itoh[s[i]];

  // Insert '-'s
  s[8] = s[13] = s[18] = s[23] = '-';

  return s.join('');
};

OPF.textToImage = function(text, fontStyle) {
    text = '{' + text + '}';
    var defineTextWidth = document.createElement('div');
    document.body.appendChild(defineTextWidth);
    defineTextWidth.innerHTML = text;
    defineTextWidth.style.position = 'absolute';
    defineTextWidth.style.visibility = 'hidden';
    defineTextWidth.style.top = '-1000';
    defineTextWidth.style.left = '-1000';

    if (!fontStyle) {
        fontStyle = '12px Tahoma';
    }
    defineTextWidth.style.font = fontStyle;
    var fontSize = defineTextWidth.style.fontSize;
    if (fontSize.match(/\d+px/g)) {
        fontSize = parseInt(fontSize.replace(/px/g, ''));
    }

    var height = defineTextWidth.clientHeight + 1;
    var width = defineTextWidth.clientWidth + 1;

    document.body.removeChild(defineTextWidth);

    var canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;
    var ctx = canvas.getContext('2d');

    ctx.strokeStyle = '#FF0000';
    ctx.lineWidth = 1;
    ctx.fillStyle = '#DEFCFA';
    ctx.fillRect(0, 0, width, height);

    ctx.fillStyle = '#000000';
    ctx.font = fontStyle;
    ctx.fillText(text, 1, height - fontSize / 3);
    var base64Src = canvas.toDataURL('image/png');

    return {
        base64Src: base64Src,
        width: width,
        height: height
    }
};

OPF.htmlToText = function(html, extensions) {
    if (OPF.isBlank(html)) {
        return html;
    }

    var i, r, text = html;

    if (extensions && extensions['preprocessing'])
        text = extensions['preprocessing'](text);

    text = text
        // Remove line breaks
        .replace(/(?:\n|\r\n|\r)/ig, " ")
        // Remove content in script tags.
        .replace(/<\s*script[^>]*>[\s\S]*?<\/script>/mig, "")
        // Remove content in style tags.
        .replace(/<\s*style[^>]*>[\s\S]*?<\/style>/mig, "")
        // Remove content in comments.
        .replace(/<!--.*?-->/mig, "")
        // Remove !DOCTYPE
        .replace(/<!DOCTYPE.*?>/ig, "");

    if (extensions && extensions['tagreplacement'])
        text = extensions['tagreplacement'](text);

    var doubleNewlineTags = ['p', 'h[1-6]', 'dl', 'dt', 'dd', 'ol', 'ul',
        'dir', 'address', 'blockquote', 'center', 'div', 'hr', 'pre', 'form',
        'textarea', 'table'];

    var singleNewlineTags = ['li', 'del', 'ins', 'fieldset', 'legend',
        'tr', 'th', 'caption', 'thead', 'tbody', 'tfoot'];

    for (i = 0; i < doubleNewlineTags.length; i++) {
        r = new RegExp('</?\\s*' + doubleNewlineTags[i] + '[^>]*>', 'ig');
        text = text.replace(r, '\n\n');
    }

    for (i = 0; i < singleNewlineTags.length; i++) {
        r = new RegExp('<\\s*' + singleNewlineTags[i] + '[^>]*>', 'ig');
        text = text.replace(r, '\n');
    }

    // Replace <br> and <br/> with a single newline
    text = text.replace(/<\s*br[^>]*\/?\s*>/ig, '\n');

    function decodeHtmlEntity(m, n) {
        // Determine the character code of the entity. Range is 0 to 65535
        // (characters in JavaScript are Unicode, and entities can represent
        // Unicode characters).
        var code;

        // Try to parse as numeric entity. This is done before named entities for
        // speed because associative array lookup in many JavaScript implementations
        // is a linear search.
        if (n.substr(0, 1) == '#') {
                // Try to parse as numeric entity
                if (n.substr(1, 1) == 'x') {
                        // Try to parse as hexadecimal
                        code = parseInt(n.substr(2), 16);
                } else {
                        // Try to parse as decimal
                        code = parseInt(n.substr(1), 10);
                }
        } else {
                // Try to parse as named entity
                code = OPF.ENTITIES_MAP[n];
        }

        // If still nothing, pass entity through
        return (code === undefined || code === NaN) ?
                '&' + n + ';' : String.fromCharCode(code);
    }

    text = text
        // Remove all remaining tags.
        .replace(/(<([^>]+)>)/ig,"")
        // Trim rightmost whitespaces for all lines
        .replace(/([^\n\S]+)\n/g,"\n")
        .replace(/([^\n\S]+)$/,"")
        // Make sure there are never more than two
        // consecutive linebreaks.
        .replace(/\n{2,}/g,"\n\n")
        // Remove newlines at the beginning of the text.
        .replace(/^\n+/,"")
        // Remove newlines at the end of the text.
        .replace(/\n+$/,"")
        // Decode HTML entities.
        .replace(/&([^;]+);/g, decodeHtmlEntity);

    if (extensions && extensions['postprocessing'])
        text = extensions['postprocessing'](text);

    return text;
};

OPF.calculateLookup = function(path, name) {
    path = OPF.ifBlank(path, '');
    name = OPF.ifBlank(name, '');
    var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-');
    var lookup = (OPF.isNotBlank(path) ? path + '.' : '') + normalizeName;
    return lookup.toLowerCase();
};

OPF.ENTITIES_MAP = {
  'nbsp' : 160,
  'iexcl' : 161,
  'cent' : 162,
  'pound' : 163,
  'curren' : 164,
  'yen' : 165,
  'brvbar' : 166,
  'sect' : 167,
  'uml' : 168,
  'copy' : 169,
  'ordf' : 170,
  'laquo' : 171,
  'not' : 172,
  'shy' : 173,
  'reg' : 174,
  'macr' : 175,
  'deg' : 176,
  'plusmn' : 177,
  'sup2' : 178,
  'sup3' : 179,
  'acute' : 180,
  'micro' : 181,
  'para' : 182,
  'middot' : 183,
  'cedil' : 184,
  'sup1' : 185,
  'ordm' : 186,
  'raquo' : 187,
  'frac14' : 188,
  'frac12' : 189,
  'frac34' : 190,
  'iquest' : 191,
  'Agrave' : 192,
  'Aacute' : 193,
  'Acirc' : 194,
  'Atilde' : 195,
  'Auml' : 196,
  'Aring' : 197,
  'AElig' : 198,
  'Ccedil' : 199,
  'Egrave' : 200,
  'Eacute' : 201,
  'Ecirc' : 202,
  'Euml' : 203,
  'Igrave' : 204,
  'Iacute' : 205,
  'Icirc' : 206,
  'Iuml' : 207,
  'ETH' : 208,
  'Ntilde' : 209,
  'Ograve' : 210,
  'Oacute' : 211,
  'Ocirc' : 212,
  'Otilde' : 213,
  'Ouml' : 214,
  'times' : 215,
  'Oslash' : 216,
  'Ugrave' : 217,
  'Uacute' : 218,
  'Ucirc' : 219,
  'Uuml' : 220,
  'Yacute' : 221,
  'THORN' : 222,
  'szlig' : 223,
  'agrave' : 224,
  'aacute' : 225,
  'acirc' : 226,
  'atilde' : 227,
  'auml' : 228,
  'aring' : 229,
  'aelig' : 230,
  'ccedil' : 231,
  'egrave' : 232,
  'eacute' : 233,
  'ecirc' : 234,
  'euml' : 235,
  'igrave' : 236,
  'iacute' : 237,
  'icirc' : 238,
  'iuml' : 239,
  'eth' : 240,
  'ntilde' : 241,
  'ograve' : 242,
  'oacute' : 243,
  'ocirc' : 244,
  'otilde' : 245,
  'ouml' : 246,
  'divide' : 247,
  'oslash' : 248,
  'ugrave' : 249,
  'uacute' : 250,
  'ucirc' : 251,
  'uuml' : 252,
  'yacute' : 253,
  'thorn' : 254,
  'yuml' : 255,
  'quot' : 34,
  'amp' : 38,
  'lt' : 60,
  'gt' : 62,
  'OElig' : 338,
  'oelig' : 339,
  'Scaron' : 352,
  'scaron' : 353,
  'Yuml' : 376,
  'circ' : 710,
  'tilde' : 732,
  'ensp' : 8194,
  'emsp' : 8195,
  'thinsp' : 8201,
  'zwnj' : 8204,
  'zwj' : 8205,
  'lrm' : 8206,
  'rlm' : 8207,
  'ndash' : 8211,
  'mdash' : 8212,
  'lsquo' : 8216,
  'rsquo' : 8217,
  'sbquo' : 8218,
  'ldquo' : 8220,
  'rdquo' : 8221,
  'bdquo' : 8222,
  'dagger' : 8224,
  'Dagger' : 8225,
  'permil' : 8240,
  'lsaquo' : 8249,
  'rsaquo' : 8250,
  'euro' : 8364
};