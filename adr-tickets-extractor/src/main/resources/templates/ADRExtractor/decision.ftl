== ${url}[${title}]

<#if status=="OPEN">
Ticket opened on ${getLastDate()?date}
<#elseif status=="CLOSED">
Ticket closed on ${getLastDate()?date}
<#else>
status is ${status}
</#if>

${text}

<#list comments as comment>
${comment.text}

</#list>