import { defineAbility } from '@casl/ability';
import {Ability, AbilityBuilder} from "@casl/ability";
import { createCanBoundTo } from "@casl/react";

const ability = new Ability([])

function updateAbilityCoursePage(ability, user) {
  const { can, rules } = new AbilityBuilder();

  if (user.role === 'teacher') {
    can(['read', 'write'], 'Project');
    can(['read', 'write'], 'Grade');
    can(['read'], 'Member');
  } else if (user.role === 'ta') {
    can(['read'], 'Project');
    can(['read'], 'Grade', {studentId: {$in: user.assigned}});
    can(['read'], 'Member');
  }

  ability.update(rules)
}

const Can = createCanBoundTo(ability)

export {Can, ability, updateAbilityCoursePage};
