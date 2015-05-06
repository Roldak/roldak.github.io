
<!-- start here -->
<p>
<div class="paper">
  <p>Please keep in mind that the miniboxing plugin is a beta release, and you may encounter occasional hickups as we are working towards a feature-complete and super-stable implementation.</p>
  <p>We are doing our best to make miniboxing a stable transformation, with <a href="https://travis-ci.org/miniboxing/miniboxing-plugin" target="_blank">nightly</a> <a href="https://travis-ci.org/miniboxing/miniboxing-example" target="_blank">builds</a> and <a href="https://github.com/miniboxing/miniboxing-plugin/tree/wip/tests/correctness/resources/miniboxing/tests/compile" target="_blank">hundreds of test cases</a> running every night. Yet, there are <a href="https://github.com/miniboxing/miniboxing-plugin/issues?state=open" target="_blank">bugs we haven&#39;t fixed yet</a>, so don&#39;t be surprised if the plugin fails on some programs. But please do <a href="/issues.html" target="_blank">file bugs</a> for such failures, so we can fix them asap! (on average, we fixed a bug every 3 days for the last two months!)</p>
  <p>The miniboxing release currently supports two specific versions of the Scala compiler:
  <img src="/images/mbox.png" alt="paper" height="100px" align="right"/>
  <ul>
    <li><b>2.11.6</b>, the current release in the 2.11 series and</li>
    <li><b>2.10.5</b>, the current release in the 2.10 series</li>
  </ul></p>
  <p>Due to compiler bugs (for the 2.10 series) and binary incompatibility of the compiler API (for the 2.11 series), we do not currently support the other versions. We are planning, however, <a href="https://github.com/miniboxing/miniboxing-plugin/issues/140" target="_blank">to fully cross-compile against all 2.10 and 2.11 versions</a> and to provide error messages for the earlier and unsupported versions of the Scala compiler. We&#39;re sorry for this, but the engineering effort in supporting even two versions of the compiler is significant!</p>
</div>
</p>

