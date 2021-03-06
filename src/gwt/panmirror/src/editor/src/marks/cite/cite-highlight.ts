/*
 * cite-highlight.ts
 *
 * Copyright (C) 2019-20 by RStudio, PBC
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

import { PluginKey } from 'prosemirror-state';
import { DecorationSet, Decoration } from 'prosemirror-view';
import { Schema } from 'prosemirror-model';

import { markHighlightPlugin, markHighlightDecorations } from '../../api/mark-highlight';

const key = new PluginKey<DecorationSet>('cite-highlight');

export function citeHighlightPlugin(schema: Schema) {
  return markHighlightPlugin(key, schema.marks.cite, (text, _attrs, markRange) => {
    // id decorations
    const kIdClass = 'pm-link-text-color';
    const re = /-?@[\w:.#$%&-+?<>~/]+/g;
    const decorations = markHighlightDecorations(markRange, text, re, kIdClass);

    // delimiter decorations
    const kDelimClass = 'pm-light-text-color';
    return decorations.concat([
      Decoration.inline(markRange.from, markRange.from + 1, { class: kDelimClass }),
      Decoration.inline(markRange.to - 1, markRange.to, { class: kDelimClass }),
    ]);
  });
}
