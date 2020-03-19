# -*- coding: utf-8 -*-

def smartest_search():
	
	xModel = XSCRIPTCONTEXT.getDocument()
	xSelectionSupplier = xModel.getCurrentController()
	xIndexAccess = xSelectionSupplier.getSelection()
	count = xIndexAccess.getCount()
	if not count:
		return None
	
	xTextRange = xIndexAccess.getByIndex(0)
	search = xModel.createSearchDescriptor()
	search.SearchString = xTextRange.getString()
	search.SearchCaseSensitive = False
	
	container = xModel.findAll(search)
	count = container.getCount()
	for i in range(count):
		found = container.getByIndex(i)
		found.String = found.String.upper()
	
	xModel.store()