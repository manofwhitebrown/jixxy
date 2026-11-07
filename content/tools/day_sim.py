# WRITER day-in-the-life hand simulation (T-W3-12). Rough: scripted events, simple caps, no mood; not the engine. Run: python3 content/tools/day_sim.py
import json,glob,random,re,collections,sys
from pathlib import Path
ROOT=str(Path(__file__).resolve().parent.parent.parent/'app/src/main/assets/lines/en')+'/'
bank=[l for f in glob.glob(ROOT+'*.json') for l in json.load(open(f))]
by=collections.defaultdict(list)
for l in bank: by[l['trigger']].append(l)
CAP={'CALM':(3,45),'NAGGY':(15,10),'CHAOS':(30,5)}
def fmt(m): return f"{m//60:02d}:{m%60:02d}"
def fill(t,app,minutes,count,now,batt):
    return t.format(app=app,minutes=minutes,count=count,time=fmt(now%1440),battery=batt) if '{' in t else t
def pick(trig,rnd,lvl,cat,tier,recent,flags,rcap=2):
    out=[]
    for l in by[trig]:
        t=set(l['tags'])
        if l['roast']>rcap or l['bondMin']>1: continue
        if lvl=='CALM' and 'nag_only' in t: continue
        if l['categories'] and cat not in l['categories']: continue
        if 'followup' in t and not flags.get('followup'): continue
        if 'followup' not in t and flags.get('followup'): continue
        if 'escalation' in t and not flags.get('esc'): continue
        if 'dropped' in t: continue
        fact=[x for x in t if x.startswith('fact:')]
        if fact and fact[0][5:] not in flags.get('facts',()): continue
        if l['id'] in recent: continue
        out.append(l)
    if not out: return None
    if trig=='PAUSE_PROMPT':
        pref=[l for l in out if f'tier{tier}' in l['tags']]
        if not pref and tier>0: pref=[l for l in out if f'tier{tier-1}' in l['tags']] 
        if not pref: pref=[l for l in out if not any(x in l['tags'] for x in('tier1','tier2'))]
        out=pref or out
        if tier>=1: out=[l for l in out if True]
    return rnd.choice(out)
SCRIPT=[(490,'open','Instagram','SOCIAL'),(492,'leave'),(525,'charger'),(570,'open','Chrome','BROWSER'),(605,'poke'),
(660,'open','Instagram','SOCIAL'),(662,'stay'),(668,'leave'),(750,'open','YouTube','VIDEO'),(752,'stay'),(758,'followup'),(765,'stay'),(771,'followup'),(780,'leave'),
(795,'session','YouTube','VIDEO',35),(840,'music'),(920,'open','Instagram','SOCIAL'),(922,'leave'),(960,'open','Instagram','SOCIAL'),(962,'leave'),
(1050,'open','BGMI','GAME'),(1052,'leave'),(1080,'session','BGMI','GAME',32),(1170,'charger'),(1230,'open','YouTube','VIDEO'),(1232,'stay'),(1238,'followup'),(1245,'leave'),
(1290,'session','YouTube','VIDEO',45),(1330,'open','Instagram','SOCIAL'),(1332,'stay'),(1338,'followup'),(1340,'leave'),
(1400,'open','Instagram','SOCIAL'),(1402,'leave'),(1430,'poke'),(1431,'poke'),(1432,'poke'),(1480,'open','Instagram','SOCIAL'),(1482,'leave')]
def run(lvl,days,seed):
    rnd=random.Random(seed); recent=collections.deque(maxlen=200); log=[]; seen=collections.Counter()
    for d in range(days):
        pauses=collections.Counter(); visits=collections.Counter(); used=0; last=-9999; leave_streak=0; ymin=0
        rows=[]
        for ev in SCRIPT:
            now=ev[0]+d*1440; kind=ev[1]; app=ev[2] if len(ev)>2 else ''; cat=ev[3] if len(ev)>3 else None
            def say(trig,facts=(),**fl):
                l=pick(trig,rnd,lvl,cat,tier,recent,dict(facts=facts,**fl)); 
                if not l: return None
                recent.append(l['id']); seen[l['id']]+=1; return l
            tier=0
            if kind=='open':
                pauses[app]+=1; visits[app]+=1; tier=min(2,pauses[app]-1)
                l=say('PAUSE_PROMPT'); 
                if l: rows.append((ev[0],'PAUSE',l,fill(l['text'],app,visits[app]*7,pauses[app],now,60)))
                if now%1440>=1380 or now%1440<60:
                    if now-last>=CAP[lvl][1] and used<CAP[lvl][0]:
                        l2=say('LATE_NIGHT')
                        if l2: used+=1; last=now; rows.append((ev[0],'late',l2,fill(l2['text'],app,0,0,now,40)))
            elif kind=='leave':
                l=say('PAUSE_LEAVE'); leave_streak+=1
                if l: rows.append((ev[0],'leave',l,fill(l['text'],app or 'app',20,1,now,50)))
            elif kind=='stay':
                l=say('PAUSE_STAY'); 
                if l: rows.append((ev[0],'stay',l,fill(l['text'],'app',20,1,now,50)))
            elif kind=='followup':
                cat=cat or 'VIDEO'; tier=0
                l=say('PAUSE_PROMPT',followup=True)
                if l: rows.append((ev[0],'followup',l,fill(l['text'],'the app',20,1,now,50)))
            else:
                trig={'charger':'CHARGER_ON','poke':'POKE','music':'MUSIC','session':'SESSION_LONG'}[kind]
                if now-last<CAP[lvl][1] or used>=CAP[lvl][0]: continue
                mins=ev[4] if kind=='session' else 0
                a=app or 'YouTube'; c=cat
                cat=c
                fl={'esc':kind=='poke' and ev[0] in(1431,1432)}
                l=say(trig,**fl)
                if l: used+=1; last=now; rows.append((ev[0],kind,l,fill(l['text'],a,mins,3,now,28)))
            # callbacks
            facts=[]
            if sum(pauses.values())>=3: facts.append('pauses_day')
            if any(v>=4 for v in visits.values()): facts.append('app_visits_day')
            if kind=='open' and facts and now-last>=CAP[lvl][1] and used<CAP[lvl][0] and rnd.random()<0.5:
                l=say('CALLBACK',facts=facts)
                if l:
                    used+=1; last=now; rows.append((ev[0]+1,'callback',l,fill(l['text'],app,45,sum(pauses.values()),now,50)))
        log.append(rows)
    return log,seen
if __name__=='__main__':
    for lvl in ('CALM','NAGGY','CHAOS'):
        log,seen=run(lvl,3,7)
        print(f'\n===== {lvl}: day 1')
        for t,k,l,txt in log[0]: print(fmt(t),k.ljust(8),f"r{l['roast']}",txt)
        un=[sum(1 for r in day if r[1] not in('PAUSE','leave','stay','followup')) for day in log]
        rep=sum(1 for v in seen.values() if v>1)
        print(f'-- unprompted per day {un}; pause lines/day {[sum(1 for r in day if r[1] in("PAUSE","leave","stay","followup")) for day in log]}; lines seen twice in 3 days: {rep}')
