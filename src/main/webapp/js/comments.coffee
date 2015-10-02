m = jQuery
now = -> Date.now()
getJAlissas = -> m('[model-id]')
trim =-> getJAlissas().each -> @value = @value.trim()

Looks = {
  Clear: ''
  Transition: 'inTransit'
  Synced: 'synced'
}

Window.Reg = []
Reg = Window.Reg

Octo = {
  getJAlissas: -> m '.cool'
}

getGreg = (alissa) ->
  id = alissa.getId()
  if Reg[id] is undefined
    Reg[id] = new Greg(id, alissa)
  else Reg[id]

class Greg
  constructor: (id, lisa) ->
    @id = id
    @lisa = lisa
    @timeStamp = now()
    @val = ''
  fireUpdateClient: ->
    @lisa.onUpdateFromServer( this )
  fireUpdateServer: ->
    Daemon.onUpdateServer( this )
  onValueChangedFromClient: (val) ->
    @val = val
    @timeStamp = now()
    @fireUpdateServer()
  onUpdateFromServer: (update) ->
    if update.timeStamp > @timeStamp
      @timeStamp = update.timeStamp
      @val = update.comment
      @fireUpdateClient()
  onSendSucceeded: ->
    @lisa.onValueSynced()

class Alissa
  constructor: (jElement) ->
    @setE jElement
    @connect()
  bindEvents: ->
    @e
    .on 'keydown', =>
      @onKD()
    .on 'paste', =>
      @onKD()

  connect: ->
    @greg = getGreg (@)
  fireValueChangedFromServer: ->
  getId: -> @e.attr 'model-id'
  getTimeStamp: ->
    time = @e.attr 'model-timeStamp'
    if time?
      if time instanceof Number then time else parseInt( time )
    else return 0
  getVal: -> @e.val()
  onKD: () ->
    # Allows to catch all  #
    oldState = @getVal()
    setTimeout =>
      newState = @getVal()
      if newState isnt oldState
        @onValueChanged()
    , 5
  onUpdateFromServer: (greg) ->
    @setLook Looks.Synced
    if greg.timeStamp > @getTimeStamp()
      @setTimeStamp greg.timeStamp
      @setVal greg.val
      @fireValueChangedFromServer()

  onValueChanged: ->
    @setTimeStamp now()
    @setLook Looks.Transition
    @greg.onValueChangedFromClient( @getVal() )
  onValueSynced: ->
    @setLook Looks.Synced
  resetLook: ->
    if @lookResetTimeoutId? then clearTimeout @lookResetTimeoutId
    # Set timeout to reset the look
    @lookResetTimeoutId = setTimeout =>
      @lookResetTimeoutId = null
      @setLook Looks.Clear
    , 3000
  setE: (jElement) ->
    @e = jElement
    @bindEvents()
  setLook: (Look)->
    switch Look
      when Looks.Clear
        @e.removeClass Looks.Synced
        @e.removeClass Looks.Transition
      when Looks.Synced
        @e.removeClass Looks.Transition
        @e.addClass Looks.Synced
        @resetLook()
      when Looks.Transition
        @e.removeClass Looks.Synced
        @e.addClass Looks.Transition
  setTimeStamp: (timeStamp = now()) ->
    @e.attr 'model-timeStamp', timeStamp
  setVal: (v) ->
    @e.val v

Daemon =
  cache: []
  onUpdateServer: (greg) ->
    @cache[greg.id] = greg
  onUpdates: (updates) ->
    for update in updates
      try
        Reg[update.id].onUpdateFromServer(update)
    return
  pull: ->
    self = this
    m.ajax
      url: pullUrl
      data:
        timeStamp: now() - 5000
      success: (data) ->
        if data.updates?
          self.onUpdates( data.updates )
  push: ->
    cache = @cache
    len = cache.length
    return  if len is 0
    @cache = []
    for key, greg of cache
      @send greg

  send: (greg) ->
    self = this
    m.ajax
      url: pushUrl
      type: 'POST'
      data:
        id: greg.id,
        comment: greg.val,
        timeStamp: greg.timeStamp
      success: ->
        greg.onSendSucceeded()
      error: ->
        self.cache[greg.id] = greg
  wakeup: ->
    @pushTimerId = setInterval =>
      @push()
    , 1000
    @pullTimerId = setInterval =>
      @pull()
    , 5000

    # Reload after 10 minutes
    setTimeout ->
      location.reload()
    , 600000

@ignite = ->
  window.pushUrl = m('#aj').attr 'href'
  window.pullUrl = m('#ajpoll').attr 'href'

  trim()
  for jAlissa in Octo.getJAlissas()
    new Alissa(m(jAlissa))
  Daemon.wakeup()

m ignite
