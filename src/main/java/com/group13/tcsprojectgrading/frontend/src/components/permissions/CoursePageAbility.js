import { defineAbility } from '@casl/ability';
import {Ability, AbilityBuilder} from "@casl/ability";
import { createCanBoundTo } from "@casl/react";

const ability = new Ability([])

function updateAbilityCoursePage(ability, user) {
  const { can, rules } = new AbilityBuilder();

  if (user.role === 'teacher') {
    can(['read', 'write'], 'Projects')
    can(['read', 'write'], 'Statistic')
  } else if (user.role === 'ta') {
    can(['read'], 'Projects')
    can(['read'], 'Statistic')
  }

  ability.update(rules)
}

const Can = createCanBoundTo(ability)

export {Can, ability, updateAbilityCoursePage};
